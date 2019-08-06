package com.zdd.login.controller;

import com.alibaba.fastjson.JSON;
import com.zdd.common.exception.LoginException;
import com.zdd.common.jwt.JWTUtils;
import com.zdd.common.randm.VerifyCodeUtils;
import com.zdd.common.util.MD5;
import com.zdd.common.util.UID;
import com.zdd.login.dao.UserDao;
import com.zdd.login.server.UserServer;
import com.zdd.pojo.ResponseResult;
import com.zdd.pojo.entity.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private UserServer userServer;
    //滑动生成验证码
    //钩子
    @RequestMapping("getCode")
    @ResponseBody
    public ResponseResult getCode(HttpServletResponse response, HttpServletRequest request){

        Cookie[] cookies = request.getCookies();//获取所有的cookies对象
        //生成一个随机5位数的字符串
        String code = VerifyCodeUtils.generateVerifyCode(5);
        System.out.println("生成的随机字符串"+code);

        ResponseResult responseResult = ResponseResult.getResponseResult();
        responseResult.setResult(code);//程序返回的结果


        String uuid16 = "CODE"+UID.getUUID16();//获取16位的uuid
        System.out.println("UUID="+uuid16);

        redisTemplate.opsForValue().set(uuid16,code);//将随机生成的验证码，放入redis中
        redisTemplate.expire(uuid16,1, TimeUnit.MINUTES);//设置redis的过期时间

        //回写cookie                                       令牌
        Cookie cookie = new Cookie("authcode",uuid16);
        cookie.setPath("/");//所有
        cookie.setDomain("localhost");
        response.addCookie(cookie);
        return  responseResult;
    }


    //点击登陆后
    @RequestMapping("login")
    @ResponseBody
    public ResponseResult login(@RequestBody Map<String,Object> map) throws LoginException {

        ResponseResult responseResult = ResponseResult.getResponseResult();

        System.out.println(map.get("codekey").toString()+"<==================codeKey");
        String code = redisTemplate.opsForValue().get(map.get("codekey").toString());//获取cookie中

        System.out.println(code+"获取cookie");
        System.out.println(map.get("loginname").toString()+"<==");

        if(code ==null ||!code.equals(map.get("code")) ){
            responseResult.setCode(500);
            responseResult.setError("验证码错误");
            return  responseResult;//测试验证码
        }
        if(map.get("loginname") != null){
            UserInfo user = userServer.findUserByName(map.get("loginname").toString());
            if (user != null){
                //比对密码
                String password = MD5.encryptPassword(map.get("password").toString(), "lcg");
                if(password.equals(user.getPassword())){
                    //将用户信息转换成JSON
                    String toJSONString = JSON.toJSONString(user);
                    System.out.println("将用户信息转换成JSON=>"+toJSONString);
                    //将用户信息转换成jwt进行加密,将加密信息作为票据
                    String generateToken = JWTUtils.generateToken(toJSONString);

                    responseResult.setToken(generateToken);

                    //将token放入redis
                    redisTemplate.opsForValue().set("USERINFO"+user.getId().toString(),generateToken);//获取id的原因是 为了标识唯一

                    //设置token过期时间
                    redisTemplate.expire("USERINFO"+user.getId().toString(),600,TimeUnit.MINUTES);

                    responseResult.setResult(user);
                    responseResult.setCode(200);
                    responseResult.setSuccess("登陆成功");
                    System.out.println(responseResult.getCode()+"getcode");
                    return responseResult;
                }else{
                    throw  new LoginException("用户名或密码错误");
                }
            }else{
                throw  new LoginException("用户名或密码错误");
            }
        }else{
            throw  new LoginException("用户名或密码错误");
        }

    }
}

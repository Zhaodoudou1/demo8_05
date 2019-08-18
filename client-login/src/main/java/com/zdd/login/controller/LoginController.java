package com.zdd.login.controller;

import com.alibaba.fastjson.JSON;
import com.zdd.common.exception.LoginException;
import com.zdd.common.jwt.JWTUtils;
import com.zdd.common.randm.VerifyCodeUtils;
import com.zdd.common.util.MD5;
import com.zdd.common.util.UID;
import com.zdd.login.dao.UserDao;
import com.zdd.login.server.CustomerService;
import com.zdd.pojo.ResponseResult;
import com.zdd.pojo.entity.UserInfo;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Controller

public class LoginController {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Autowired
    private CustomerService customerService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserDao userDao;
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
    
    public ResponseResult  responseResult (){
        return ResponseResult.getResponseResult();
    }

    //点击登陆后
    @RequestMapping("login")
    @ResponseBody
    public ResponseResult login(@RequestBody Map<String,Object> map) throws LoginException {

        ResponseResult responseResult = this.responseResult();

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
            UserInfo user = customerService.getUserByLogin(map.get("loginname").toString());
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
                    System.out.println("user.getAuthmap()"+user.getAuthmap());
                    redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId().toString(),user.getAuthmap());
                    //设置token过期时间
                    redisTemplate.expire("USERINFO"+user.getId().toString(),600,TimeUnit.MINUTES);

                    //折线图


                    String fmd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

                    if(!redisTemplate.hasKey(fmd+user.getId())){
                        redisTemplate.opsForValue().set(fmd+user.getId(),"",1,TimeUnit.DAYS);
                        stringRedisTemplate.opsForHash().increment("loginCount",fmd,1);
                    }
                    Object[] loginCounts = redisTemplate.opsForHash().keys("loginCount").toArray();

                    Object[] values = redisTemplate.opsForHash().values("loginCount").toArray();


                    user.setLoginKeys(loginCounts);
                    user.setLoginValues(values);

                    responseResult.setResult(user);

                     responseResult.setCode(200);
                    responseResult.setSuccess("登陆成功");
                    System.out.println(responseResult.getCode()+"getcode");
                    System.out.println(user+"用户信息=============");
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







        public  void commonality(UserInfo user){
            ResponseResult responseResult = this.responseResult();
            System.out.println("=============================================================================");
            //将用户信息转换成JSON
            String toJSONString = JSON.toJSONString(user);
            System.out.println("将用户信息转换成JSON=>"+toJSONString);
            //将用户信息转换成jwt进行加密,将加密信息作为票据
            String generateToken = JWTUtils.generateToken(toJSONString);

            responseResult.setToken(generateToken);

            //将token放入redis
            redisTemplate.opsForValue().set("USERINFO"+user.getId().toString(),generateToken);//获取id的原因是 为了标识唯一
            System.out.println("user.getAuthmap()"+user.getAuthmap());
            redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId().toString(),user.getAuthmap());
            //设置token过期时间
            redisTemplate.expire("USERINFO"+user.getId().toString(),600,TimeUnit.MINUTES);

            //折线图


            String fmd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if(!redisTemplate.hasKey(fmd+user.getId())){
                redisTemplate.opsForValue().set(fmd+user.getId(),"",1,TimeUnit.DAYS);
                stringRedisTemplate.opsForHash().increment("loginCount",fmd,1);
            }
            Object[] loginCounts = redisTemplate.opsForHash().keys("loginCount").toArray();

            Object[] values = redisTemplate.opsForHash().values("loginCount").toArray();


            user.setLoginKeys(loginCounts);
            user.setLoginValues(values);

            responseResult.setResult(user);
        }













    @RequestMapping("loginout")
    @ResponseBody
    public ResponseResult loginOut(String loginName){
        //根据用户名获取用户信息
        UserInfo user = userDao.findByLoginName(loginName);
        System.out.println(user);
        if(user!=null){
            redisTemplate.delete("USERDATAAUTH"+user.getId().toString());
            redisTemplate.delete("USERINFO"+user.getId());
        }
        ResponseResult responseResult = ResponseResult.getResponseResult ();
        responseResult.setSuccess ( "ok" );
        return responseResult;
    }

    @RequestMapping("selectUserName")
    @ResponseBody
    public ResponseResult selectUserName(@RequestBody String loginName){
        System.out.println("账号验证:"+loginName);
        ResponseResult responseResult = ResponseResult.getResponseResult();
        UserInfo userInfo = customerService.selectUserName(loginName);
        if(userInfo != null){
            responseResult.setCode(500);
        }else{
            responseResult.setCode(200);
        }

        return responseResult;
    }



    //生成验证码
    @RequestMapping("selphone")
    public ResponseResult phone(@RequestBody Map<String,Object> map){
        ResponseResult responseResult = new ResponseResult();
        String tel=map.get("tel").toString();
        System.out.println(tel+"获取手机号");
        if(tel!=null&&tel!=""){
            String authcode = customerService.getAuthcode(tel);
            System.out.println("==>"+authcode);
            redisTemplate.opsForValue().set(tel,authcode);
            redisTemplate.expire(tel,1, TimeUnit.MINUTES);
            System.out.println("aaaaa=-----------"+authcode);
            responseResult.setCode(200);
            responseResult.setSuccess("获取验证码成功请在60秒内登陆");
        }else {
            System.out.println("失败");
            responseResult.setCode(500);
            responseResult.setError("获取验证码失败");
        }

        return  responseResult;
    }



    @RequestMapping("loginphone")
    @ResponseBody
    public ResponseResult  toLoginByPhone(@RequestBody Map<String,Object> map) throws LoginException {
        System.out.println(map.get("tel").toString()+"[hone");
        System.out.println(map.get("authcode").toString()+"----------------------------------authcode");
        String tel=map.get("tel").toString();
        String authcode=map.get("authcode").toString();
        String aaa = (String) redisTemplate.opsForValue().get(tel);
        System.out.println(aaa);
        if(aaa==null||aaa.equals("")|| !aaa.equals(authcode)){
            throw new LoginException("手机或验证码错误");
        }
        ResponseResult responseResult=ResponseResult.getResponseResult();

        //根据手机号获取到用户信息
        UserInfo user = customerService.selPhone(map.get("tel").toString());
        if(user!=null){
            System.out.println("<====>");
            System.out.println(user);
            UserInfo byLoginName = customerService.getUserByLogin(user.getLoginName());

            //将用户信息转换成JSON
            String toJSONString = JSON.toJSONString(user);
            System.out.println("将用户信息转换成JSON=>"+toJSONString);
            //将用户信息转换成jwt进行加密,将加密信息作为票据
            String generateToken = JWTUtils.generateToken(toJSONString);

            responseResult.setToken(generateToken);

            //将token放入redis
            redisTemplate.opsForValue().set("USERINFO"+user.getId().toString(),generateToken);//获取id的原因是 为了标识唯一
            System.out.println("user.getAuthmap()"+user.getAuthmap());
            redisTemplate.opsForHash().putAll("USERDATAAUTH"+user.getId().toString(),user.getAuthmap());
            //设置token过期时间
            redisTemplate.expire("USERINFO"+user.getId().toString(),600,TimeUnit.MINUTES);

            //折线图


            String fmd = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

            if(!redisTemplate.hasKey(fmd+user.getId())){
                redisTemplate.opsForValue().set(fmd+user.getId(),"",1,TimeUnit.DAYS);
                stringRedisTemplate.opsForHash().increment("loginCount",fmd,1);
            }
            Object[] loginCounts = redisTemplate.opsForHash().keys("loginCount").toArray();

            Object[] values = redisTemplate.opsForHash().values("loginCount").toArray();


            user.setLoginKeys(loginCounts);
            user.setLoginValues(values);

            responseResult.setResult(user);

            redisTemplate.delete(user.getTel());//在登陆成功后删除redis储存的手机验证码
            //返回正确的值
            responseResult.setCode(200);
            responseResult.setSuccess("登陆成功");
            return responseResult;

        }else {
            throw new LoginException("手机或验证码错误");
        }





    }
}

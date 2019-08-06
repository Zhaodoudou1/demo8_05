package com.zdd.server.filter;

import com.zdd.common.jwt.JWTUtils;
import io.jsonwebtoken.JwtException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

//设置全局过滤器
@Component
public class MyGlobalFilter implements GlobalFilter {

    @Value("${my.auth.urls}")
    private String[] url;//不过滤的地址

    @Value("${my.auth.loginPath}")
    private String loginPath;//登陆界面的配置
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("------------------全局过滤器--------------------");
        //先获取requse
        ServerHttpRequest request = exchange.getRequest();//请求
        ServerHttpResponse response = exchange.getResponse();//响应

        //获取当前请求url
        String url = request.getURI().toString();

        List<String> strings = Arrays.asList(url);//该方法是将数组转化为list  方法将数组与列表链接起来
        //验证当前路径是否是公共路径不需要校验
                    //contains==>包含
        if(strings.contains(url)){
            return chain.filter(exchange);
        }else{
            List<String> token = request.getHeaders().get("token");//获取token 信息

            JSONObject jsonObject = null;
            try {
                    //前 使用了jwt 进行了加密    -》调用jwt工具类中的解密方法进行jwt解密
                com.alibaba.fastjson.JSONObject jsonObject1 = JWTUtils.decodeJwtTocken(token.get(0));
                //如果没报错说明没有失效
                //在重新加密
                String generateToken = JWTUtils.generateToken(jsonObject1.toJSONString());

                //放入响应头中
                response.getHeaders().set("token",generateToken);
            }catch (JwtException e){
                    e.printStackTrace();

                    //token 过期时间是30分钟  为防止长时间未登陆还在使用旧的token
                    //后跳转到登陆页面
                    response.getHeaders().set("Location",loginPath);
                    response.setStatusCode(HttpStatus.SEE_OTHER);
                    return  exchange.getResponse().setComplete();
            }
        }
        return null;
    }
}

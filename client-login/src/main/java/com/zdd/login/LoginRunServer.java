package com.zdd.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EntityScan(basePackages = "com.zdd.pojo.**")
public class LoginRunServer {
    public static void main(String[] args) {
        SpringApplication.run(LoginRunServer.class,args);
    }

    @RequestMapping("health")
    public String health(){
        System.out.println("health   login 健康检查");
        return "ok";
    }
}

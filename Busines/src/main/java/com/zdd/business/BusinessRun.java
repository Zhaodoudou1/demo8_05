package com.zdd.business;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class BusinessRun {

    public static void main(String[] args) {
        SpringApplication.run(BusinessRun.class,args);
    }

    @RequestMapping("health")
    public String health(){

        System.out.println("业务层健康检查");

        return "ok";
    }
}

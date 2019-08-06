package com.zdd.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GetawayServerRun {

    public static void main(String[] args) {
        SpringApplication.run(GetawayServerRun.class,args);
    }

    @RequestMapping("serverHealth")
    public String health(){
        System.out.println("health   serverGetaway 健康检查");
        return "ok";
    }
}

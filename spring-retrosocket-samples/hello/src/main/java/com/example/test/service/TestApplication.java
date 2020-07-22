package com.example.test.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "service");
        System.setProperty("spring.rsocket.server.port", "8888");
        SpringApplication.run(TestApplication.class, args);
    }

}


@Controller
@Log4j2
class GreetingsController {

    @PostConstruct
    public void construct() {
        log.info("construct()");
    }

    @MessageMapping("hello")
    String hello(String name) {
        return "Hello, " + name + "!";
    }
}


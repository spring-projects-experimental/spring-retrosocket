package com.example.test.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@SpringBootApplication
public class TestApplication {

    public static void main(String[] args) {
        System.setProperty("spring.profiles.active", "service");
        System.setProperty("spring.rsocket.server.port", "8888");
        SpringApplication.run(TestApplication.class, args);
    }

}


@Controller
class GreetingsController {

    @MessageMapping("hello")
    String hello(String name) {
        return "Hello, " + name + "!";
    }
}


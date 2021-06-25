package com.example.test.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClient;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EnableRSocketClients
@SpringBootApplication
public class TestApplication {

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.connectTcp("localhost", 8888).block();
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
        System.in.read();
    }

}

@Component
class Client {

    private final GreetingsClient client;

    Client(GreetingsClient client) {
        this.client = client;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void ready() {
        Mono<String> world = this.client.hello(Mono.just("World"));
        world.subscribe(System.out::println);

    }
}

@RSocketClient
interface GreetingsClient {

    @MessageMapping("hello")
    Mono<String> hello(Mono<String> name);
}
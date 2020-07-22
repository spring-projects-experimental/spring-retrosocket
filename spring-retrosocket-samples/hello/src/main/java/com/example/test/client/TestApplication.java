package com.example.test.client;

import com.joshlong.rsocket.client.EnableRSocketClients;
import com.joshlong.rsocket.client.RSocketClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EnableRSocketClients
@SpringBootApplication
public class TestApplication {

    @Bean
    RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
        return builder.connectTcp("localhost", 8888).block();
    }

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(TestApplication.class, args);
        System.in.read();
    }

}

@Component
@RequiredArgsConstructor
class Client {

    private final GreetingsClient client;

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
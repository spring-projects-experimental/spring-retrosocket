package com.example.test.client;

import lombok.SneakyThrows;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClient;
import reactor.core.publisher.Mono;

@EnableRSocketClients
@SpringBootApplication
public class TestApplication {

	@Bean
	RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
		return builder.tcp("localhost", 8888);
	}

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(TestApplication.class, args);
		Thread.currentThread().join();
	}

	@Bean
	ApplicationRunner runner(GreetingsClient greetingsClient) {
		return args -> greetingsClient.hello(Mono.just("world!")).subscribe(System.out::println);
	}

}

@RSocketClient
interface GreetingsClient {

	@MessageMapping("hello")
	Mono<String> hello(Mono<String> name);
}
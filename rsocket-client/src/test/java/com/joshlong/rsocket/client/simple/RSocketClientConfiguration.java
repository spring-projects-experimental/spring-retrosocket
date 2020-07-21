package com.joshlong.rsocket.client.simple;

import com.joshlong.rsocket.client.EnableRSocketClients;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;

import javax.annotation.PostConstruct;

@Log4j2
@Profile("client")
@EnableRSocketClients
@SpringBootApplication
class RSocketClientConfiguration {

	@PostConstruct
	public void start() {
		log.info("starting " + RSocketClientConfiguration.class.getName() + '.');
	}

	@Bean
	RSocketRequester rSocketRequester(@Value("${service.port}") int port, RSocketRequester.Builder builder) {
		return builder.connectTcp("localhost", port).block();
	}

}

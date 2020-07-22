package com.joshlong.rsocket.client.simple;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Log4j2
@Profile("service")
@Configuration
@EnableAutoConfiguration
class RSocketServerConfiguration {

	@Bean
	GreetingsController greetingsController() {
		return new GreetingsController();
	}

	@PostConstruct
	public void start() {
		log.info("starting " + RSocketServerConfiguration.class.getName() + '.');
	}

}

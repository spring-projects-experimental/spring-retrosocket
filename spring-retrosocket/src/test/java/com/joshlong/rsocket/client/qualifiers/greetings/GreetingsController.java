package com.joshlong.rsocket.client.qualifiers.greetings;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Log4j2
@Profile("service")
@Controller
public class GreetingsController {

	@PostConstruct
	public void go() {
		log.info(getClass().getName());
	}

	@MessageMapping("greetings-with-name")
	Mono<Greeting> greetMono(Mono<String> name) {
		return name.map(Greeting::new);
	}

}

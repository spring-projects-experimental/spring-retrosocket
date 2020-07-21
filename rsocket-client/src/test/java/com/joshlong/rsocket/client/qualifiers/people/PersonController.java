package com.joshlong.rsocket.client.qualifiers.people;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;

@Log4j2
@Profile("service")
@Controller
public class PersonController {

	@PostConstruct
	public void go() {
		log.info(getClass().getName());
	}

	@MessageMapping("people")
	Flux<Person> people() {
		return Flux.just(new Person("Yuxin"), new Person("Jane"), new Person("John"), new Person("Sergei"));
	}

}

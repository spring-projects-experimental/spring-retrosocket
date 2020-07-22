package com.joshlong.rsocket.client.simple;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

@Log4j2
@Profile("service")
@Controller
class GreetingsController {

	static AtomicReference<String> fireAndForget = new AtomicReference<>();

	@PostConstruct
	public void begin() {
		log.info("begin()");
	}

	@MessageMapping("greetings-mono-name.{name}.{age}")
	Mono<String> greetMonoNameDestinationVariable(@DestinationVariable("name") String name,
			@DestinationVariable("age") int age, @Payload Mono<String> payload) {
		log.info("name=" + name);
		log.info("age=" + age);
		return payload;
	}

	@MessageMapping("fire-and-forget")
	Mono<Void> fireAndForget(Mono<String> valueIn) {
		return valueIn//
				.doOnNext(value -> {
					log.info("received fire-and-forget " + value + '.');
					fireAndForget.set(value);
				})//
				.then();
	}

	@MessageMapping("greetings-with-channel")
	Flux<GreetingResponse> greetParams(Flux<String> names) {
		return names.map(String::toUpperCase).map(GreetingResponse::new);
	}

	@MessageMapping("greetings-stream")
	Flux<GreetingResponse> greetFlux(Mono<String> name) {
		return name.flatMapMany(
				leNom -> Flux.fromStream(Stream.generate(() -> new GreetingResponse(leNom.toUpperCase()))).take(2));
	}

	@MessageMapping("greetings-with-name")
	Mono<GreetingResponse> greetMono(Mono<String> name) {
		return name.map(GreetingResponse::new);
	}

	@MessageMapping("greetings")
	Mono<GreetingResponse> greet() {
		log.info("invoking greetings and returning a GreetingsResponse.");
		return Mono.just(new GreetingResponse("Hello, world!"));
	}

}

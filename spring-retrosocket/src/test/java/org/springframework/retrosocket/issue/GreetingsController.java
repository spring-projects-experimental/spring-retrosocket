package org.springframework.retrosocket.issue;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;
import javax.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Profile("service")
@Controller
class GreetingsController {

	static AtomicReference<String> fireAndForget = new AtomicReference<>();

	@PostConstruct
	public void begin() {
		log.info("begin()");
	}

	@MessageMapping("greetings-mono-name.{name}")
	Mono<String> greetMonoNameDestinationVariable(@DestinationVariable("name") String name, @Payload Mono<String> payload) {
		log.info("name=" + name);
		return payload;
	}

	@MessageMapping("greetings-mono-name")
	Mono<String> greetMonoWithOnePayload(@Payload Mono<String> payload) {
		return payload;
	}

}

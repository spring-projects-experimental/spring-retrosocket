package com.joshlong.rsocket.client.metadata;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Map;

@Log4j2
@Profile("service")
@Controller
class GreetingsController {

	@MessageMapping("greetings")
	Mono<Map<String, Object>> greet(@Headers Map<String, Object> headers, @Payload Mono<String> in) {
		headers.forEach((k, v) -> log.info(k + '=' + v));
		Map<String, Object> data = Collections.singletonMap(Constants.CLIENT_ID_MIME_TYPE_VALUE,
				headers.get(Constants.CLIENT_ID_HEADER));
		return Mono.just(data);
	}

}

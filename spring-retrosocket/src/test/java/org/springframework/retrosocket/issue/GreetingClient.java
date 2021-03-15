package org.springframework.retrosocket.issue;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retrosocket.RSocketClient;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RSocketClient
interface GreetingClient {

	@MessageMapping("greetings-mono-name.{name}")
	Mono<String> greetMonoWithOnlyOneDestinationVariable(@DestinationVariable("name") String name);

	@MessageMapping("greetings-mono-name")
	Mono<String> greetMonoWithOnlyOnePayload(@Payload Mono<String> payload);

}

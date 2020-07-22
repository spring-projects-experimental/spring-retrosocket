package com.joshlong.rsocket.client.qualifiers.greetings;

import com.joshlong.rsocket.client.RSocketClient;
import com.joshlong.rsocket.client.qualifiers.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Mono;

@RSocketClient
@Qualifier(Constants.QUALIFIER_2)
public interface GreetingClient {

	@MessageMapping("greetings-with-name")
	Mono<Greeting> greetMono(Mono<String> name);

}

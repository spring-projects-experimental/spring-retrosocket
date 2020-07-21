package com.joshlong.rsocket.client.qualifiers.people;

import com.joshlong.rsocket.client.RSocketClient;
import com.joshlong.rsocket.client.qualifiers.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Flux;

@RSocketClient
@Qualifier(Constants.QUALIFIER_1)
public interface PersonClient {

	@MessageMapping("people")
	Flux<Person> people();

}

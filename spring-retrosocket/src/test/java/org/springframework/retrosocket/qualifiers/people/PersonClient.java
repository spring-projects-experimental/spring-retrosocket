package org.springframework.retrosocket.qualifiers.people;

import org.springframework.retrosocket.RSocketClient;
import org.springframework.retrosocket.qualifiers.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import reactor.core.publisher.Flux;

@RSocketClient
@Qualifier(Constants.QUALIFIER_1)
public interface PersonClient {

	@MessageMapping("people")
	Flux<Person> people();

}

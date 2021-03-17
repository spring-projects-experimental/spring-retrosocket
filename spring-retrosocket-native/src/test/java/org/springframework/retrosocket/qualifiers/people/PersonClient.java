package org.springframework.retrosocket.qualifiers.people;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.retrosocket.qualifiers.Constants;
import reactor.core.publisher.Flux;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Qualifier(Constants.QUALIFIER_1)
public interface PersonClient {

	@MessageMapping("people")
	Flux<Person> people();

}

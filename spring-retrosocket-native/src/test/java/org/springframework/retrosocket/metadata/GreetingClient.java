package org.springframework.retrosocket.metadata;

import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
interface GreetingClient {

	@MessageMapping("greetings")
	Mono<Map<String, Object>> greet(@Header(Constants.CLIENT_ID_MIME_TYPE_VALUE) String clientId,
			@Payload Mono<String> name);

}

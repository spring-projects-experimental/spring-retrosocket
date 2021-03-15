package org.springframework.retrosocket.metadata;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.retrosocket.EnableRSocketClients;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Profile("client")
@EnableRSocketClients
@SpringBootApplication
class RSocketClientConfiguration {

	@Bean
	RSocketRequester rSocketRequester(@Value("${service.port}") int port, RSocketRequester.Builder builder) {
		return builder.connectTcp("localhost", port).block();
	}

}

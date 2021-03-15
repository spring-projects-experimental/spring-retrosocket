package org.springframework.retrosocket.qualifiers;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.retrosocket.EnableRSocketClients;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Profile("client")
@EnableRSocketClients
@SpringBootApplication
class RSocketClientConfiguration {

	// people
	@Bean
	@PersonQualifier
	// @Qualifier(Constants.QUALIFIER_1)
	RSocketRequester one(@Value("${" + Constants.QUALIFIER_1 + ".port}") int port, RSocketRequester.Builder builder) {
		return builder.connectTcp("localhost", port).block();
	}

	// greetings
	@Bean
	@Qualifier(Constants.QUALIFIER_2)
	// @GreetingQualifier
	RSocketRequester two(@Value("${" + Constants.QUALIFIER_2 + ".port}") int port, RSocketRequester.Builder builder) {
		return builder.connectTcp("localhost", port).block();
	}

}

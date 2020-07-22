package org.springframework.retrosocket.qualifiers;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.retrosocket.qualifiers.greetings.GreetingsController;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Profile("service2")
@Configuration
@EnableAutoConfiguration
class GreetingServiceConfiguration {

	@Bean
	GreetingsController greetingsController() {
		return new GreetingsController();
	}

}

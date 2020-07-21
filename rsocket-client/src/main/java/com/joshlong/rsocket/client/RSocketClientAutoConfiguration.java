package com.joshlong.rsocket.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.rsocket.RSocketRequester;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Configuration
class RSocketClientAutoConfiguration {

	@Bean
	@ConditionalOnBean(RSocketRequester.class)
	RSocketClientBuilder rSocketClientBuilder() {
		return new RSocketClientBuilder();
	}

}

package org.springframework.retrosocket.metadata;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.rsocket.messaging.RSocketStrategiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.codec.StringDecoder;

import javax.annotation.PostConstruct;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@Profile("service")
@Configuration
@EnableAutoConfiguration
class RSocketServerConfiguration {

	@Bean
	RSocketStrategiesCustomizer rSocketStrategiesCustomizer() {
		return strategies -> strategies//
				.metadataExtractorRegistry(registry -> {
					registry.metadataToExtract(Constants.CLIENT_ID_MIME_TYPE, String.class, Constants.CLIENT_ID_HEADER);
					registry.metadataToExtract(Constants.LANG_MIME_TYPE, String.class, Constants.LANG_HEADER);
				})//
				.decoders(decoders -> decoders.add(StringDecoder.allMimeTypes()));
	}

	@Bean
	GreetingsController greetingsController() {
		return new GreetingsController();
	}

	@PostConstruct
	public void start() {
		log.info("starting " + RSocketServerConfiguration.class.getName() + '.');
	}

}

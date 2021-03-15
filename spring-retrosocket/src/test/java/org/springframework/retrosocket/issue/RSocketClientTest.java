package org.springframework.retrosocket.issue;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */

@Log4j2
public class RSocketClientTest {

	static ConfigurableApplicationContext serviceApplicationContext;

	static AtomicInteger port = new AtomicInteger(SocketUtils.findAvailableTcpPort());

	@BeforeAll
	public static void begin() {
		serviceApplicationContext = new SpringApplicationBuilder(RSocketServerConfiguration.class)//
				.web(WebApplicationType.NONE)
				.run("--spring.profiles.active=service", "--spring.rsocket.server.port=" + port.get());
	}

	@AfterAll
	public static void destroy() {
		serviceApplicationContext.stop();
	}

	@Test
	public void greetMonoWithOnlyOneDestinationVariable_withOneAnnotatedArgument_shouldNotBeAPayload() {
		GreetingClient greetingClient = buildClient();
		Mono<String> greetingResponseFlux = greetingClient.greetMonoWithOnlyOneDestinationVariable("jlong");
		StepVerifier//
				.create(greetingResponseFlux)//
				.consumeNextWith(s -> {
					assertThat(s).isNotEqualTo("jslong");
					assertThat(s).isEmpty();
				}).verifyComplete();
	}

	@Test
	public void greetMonoWithOnlyOnePayload_withOneAnnotatedArgumentPayload_shouldBeAPayload() {
		GreetingClient greetingClient = buildClient();
		Mono<String> greetingResponseFlux = greetingClient.greetMonoWithOnlyOnePayload(Mono.just("Hello, world!"));
		StepVerifier//
				.create(greetingResponseFlux)//
				.expectNext("Hello, world!").verifyComplete();
	}

	private GreetingClient buildClient() {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(RSocketClientConfiguration.class)//
				.web(WebApplicationType.NONE)//
				.run("--service.port=" + port.get(), "--spring.profiles.active=client");
		return context.getBean(GreetingClient.class);
	}

}

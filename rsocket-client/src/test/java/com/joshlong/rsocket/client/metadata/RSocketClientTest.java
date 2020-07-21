package com.joshlong.rsocket.client.metadata;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.SocketUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Log4j2
public class RSocketClientTest {

	@Test
	public void monoInAndOut() {
		int servicePort = SocketUtils.findAvailableTcpPort();
		ConfigurableApplicationContext service = runService(servicePort);
		ConfigurableApplicationContext client = runClient(servicePort);
		RSocketRequester rSocketRequester = client.getBean(RSocketRequester.class);
		GreetingClient gc = client.getBean(GreetingClient.class);
		StepVerifier.create(gc.greet("123", Mono.just("A Name")))
				.expectNextMatches(
						map -> map.containsKey(Constants.CLIENT_ID_MIME_TYPE_VALUE) && map.containsValue("123"))
				.verifyComplete();
		client.stop();
		service.stop();
	}

	private static ConfigurableApplicationContext runService(int port) {
		return new SpringApplicationBuilder(RSocketServerConfiguration.class)//
				.web(WebApplicationType.NONE)
				.run("--spring.profiles.active=service", "--spring.rsocket.server.port=" + port);
	}

	private ConfigurableApplicationContext runClient(int port) {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(RSocketClientConfiguration.class)//
				.web(WebApplicationType.NONE)//
				.run("--service.port=" + port, "--spring.profiles.active=client");
		return context;
	}

}

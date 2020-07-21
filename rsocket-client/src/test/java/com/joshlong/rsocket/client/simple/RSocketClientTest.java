package com.joshlong.rsocket.client.simple;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.util.SocketUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

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
	public void destinationVariablesAndPayload() {
		GreetingClient greetingClient = buildClient();
		Mono<String> greetingResponseFlux = greetingClient.greetMonoNameDestinationVariable("jlong", 36,
				Mono.just("Hello"));
		StepVerifier//
				.create(greetingResponseFlux)//
				.expectNextMatches(gr -> gr.equalsIgnoreCase("Hello"))//
				.verifyComplete();
	}

	@Test
	public void monoInFluxOut() {
		GreetingClient greetingClient = buildClient();
		Flux<GreetingResponse> greetingResponseFlux = greetingClient.greetStream(Mono.just("a"));
		StepVerifier//
				.create(greetingResponseFlux)//
				.expectNextCount(1).expectNextMatches(gr -> gr.getMessage().equalsIgnoreCase("A"))//
				.verifyComplete();

	}

	@Test
	public void fluxInFluxOut() {
		GreetingClient greetingClient = buildClient();
		Flux<GreetingResponse> greetingResponseFlux = greetingClient.greetParams(Flux.just("a", "b"));
		StepVerifier//
				.create(greetingResponseFlux)//
				.expectNextMatches(gr -> gr.getMessage().equalsIgnoreCase("A"))//
				.expectNextMatches(gr -> gr.getMessage().equalsIgnoreCase("B"))//
				.verifyComplete();

	}

	@Test
	public void monoInMonoOut() {
		GreetingClient greetingClient = buildClient();
		Mono<GreetingResponse> greet = greetingClient.greet(Mono.just("Hello, Mario"));
		StepVerifier//
				.create(greet)//
				.expectNextMatches(gr -> gr.getMessage().equalsIgnoreCase("Hello, Mario"))//
				.verifyComplete();

	}

	@Test
	public void noValueInMonoOut() throws Exception {
		GreetingClient greetingClient = buildClient();
		Mono<GreetingResponse> greet = greetingClient.greet();
		StepVerifier//
				.create(greet)//
				.expectNextMatches(gr -> gr.getMessage().equalsIgnoreCase("Hello, world!"))//
				.verifyComplete();

	}

	@Test
	public void fireAndForget() throws Exception {
		GreetingClient greetingClient = buildClient();
		String name = "Kimly";
		Mono<Void> greet = greetingClient.greetFireAndForget(Mono.just(name));
		StepVerifier//
				.create(greet)//
				.verifyComplete();

		Thread.sleep(1000);
		boolean kimly = GreetingsController.fireAndForget.get().equalsIgnoreCase(name);
		Assertions.assertTrue(kimly, "the name perceived on the service is the same as we sent.");
	}

	private GreetingClient buildClient() {
		ConfigurableApplicationContext context = new SpringApplicationBuilder(RSocketClientConfiguration.class)//
				.web(WebApplicationType.NONE)//
				.run("--service.port=" + port.get(), "--spring.profiles.active=client");
		return context.getBean(GreetingClient.class);
	}

}

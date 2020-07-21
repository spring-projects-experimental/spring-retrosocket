package com.joshlong.rsocket.client.qualifiers;

import com.joshlong.rsocket.client.EnableRSocketClients;
import com.joshlong.rsocket.client.qualifiers.greetings.GreetingClient;
import com.joshlong.rsocket.client.qualifiers.greetings.GreetingsController;
import com.joshlong.rsocket.client.qualifiers.people.PersonClient;
import com.joshlong.rsocket.client.qualifiers.people.PersonController;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.Assert;
import org.springframework.util.SocketUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier(Constants.QUALIFIER_1)
@interface PersonQualifier {

}

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Qualifier(Constants.QUALIFIER_2)
@interface GreetingQualifier {

}

@Log4j2
public class RSocketQualifierClientTest {

	private static final int port1 = SocketUtils.findAvailableTcpPort();

	private static final int port2 = SocketUtils.findAvailableTcpPort();
	static String SERVICE_1 = Constants.QUALIFIER_1;
	static String SERVICE_2 = Constants.QUALIFIER_2;
	static ConfigurableApplicationContext serviceApplicationContext1;
	static ConfigurableApplicationContext serviceApplicationContext2;

	private static ConfigurableApplicationContext runServer(Class<?> clzz, String profileName, int port) {
		return new SpringApplicationBuilder(clzz)//
				.web(WebApplicationType.NONE)//
				.run("--spring.profiles.active=" + profileName, "--spring.rsocket.server.port=" + port);
	}

	@BeforeAll
	public static void begin() {
		serviceApplicationContext1 = runServer(PeopleServerConfiguration.class, SERVICE_1, port1);
		serviceApplicationContext2 = runServer(GreetingServiceConfiguration.class, SERVICE_2, port2);
	}

	@AfterAll
	public static void destroy() {
		serviceApplicationContext1.stop();
		serviceApplicationContext2.stop();
	}

	@Test
	public void noValueInMonoOut() {
		ConfigurableApplicationContext svc = buildClient();
		PersonClient personClient = svc.getBean(PersonClient.class);
		GreetingClient greetingClient = svc.getBean(GreetingClient.class);
		Assert.notNull(personClient, "the " + PersonClient.class.getName() + " is not null");
		Assert.notNull(greetingClient, "the " + GreetingClient.class.getName() + " is not null");

		StepVerifier.create(greetingClient.greetMono(Mono.just("Spring"))).expectNextCount(1).verifyComplete();
		StepVerifier.create(personClient.people()).expectNextCount(4).verifyComplete();
	}

	private ConfigurableApplicationContext buildClient() {
		return new SpringApplicationBuilder(RSocketClientConfiguration.class)//
				.web(WebApplicationType.NONE)//
				.run("--service1.port=" + port1, "--service2.port=" + port2, "--spring.profiles.active=client");
	}

}

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

@Log4j2
@Profile("service1")
@Configuration
@EnableAutoConfiguration
class PeopleServerConfiguration {

	@Bean
	PersonController personController() {
		return new PersonController();
	}

}

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

package org.springframework.retrosocket.qualifiers;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.retrosocket.qualifiers.greetings.GreetingClient;
import org.springframework.retrosocket.qualifiers.people.PersonClient;
import org.springframework.util.Assert;
import org.springframework.util.SocketUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
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

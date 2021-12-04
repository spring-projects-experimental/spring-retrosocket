package com.example.test.client;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.nativex.hint.JdkProxyHint;
import org.springframework.nativex.hint.NativeHint;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.nativex.hint.TypeHint;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClient;
import org.springframework.retrosocket.RSocketClientFactoryBean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@NativeHint(
	types = @TypeHint(
		access = {
			TypeAccess.DECLARED_CLASSES,
			TypeAccess.DECLARED_METHODS,
			TypeAccess.DECLARED_CONSTRUCTORS,
			TypeAccess.DECLARED_FIELDS,

		},
		types = {
			Mono.class,
			String.class,
			GreetingsClient.class,

		}
	),
	jdkProxies = @JdkProxyHint(
		types = {

			GreetingsClient.class,
			org.springframework.aop.SpringProxy.class,
			org.springframework.aop.framework.Advised.class,
			org.springframework.core.DecoratingProxy.class
		}
	)
)
@EnableRSocketClients
@SpringBootApplication
public class TestApplication {

/*
	@Bean
	GreetingsClient greetingsClient(BeanFactory factory) {
		RSocketClientFactoryBean factoryBean = new RSocketClientFactoryBean();
		factoryBean.setBeanFactory(factory);
		factoryBean.setType(GreetingsClient.class.getName());
		return (GreetingsClient) factoryBean.getObject();
	}
*/

	@Bean
	RSocketRequester rSocketRequester(RSocketRequester.Builder builder) {
		return builder.tcp("localhost", 8888);
	}

/*
	@Bean
	GreetingsClient greetingsClient(RSocketRequester rSocketRequester) {
		return new RSocketClientBuilder()
			.buildClientFor(GreetingsClient.class, rSocketRequester);
	}
*/

	public static void main(String[] args) throws Exception {
		SpringApplication.run(TestApplication.class, args);
		System.in.read();
	}

}

@Component
class Client {

	private final GreetingsClient client;

	Client(GreetingsClient client) {
		this.client = client;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void ready() {
		Mono<String> world = this.client.hello(Mono.just("World"));
		world.subscribe(System.out::println);

	}
}


@RSocketClient
interface GreetingsClient {

	@MessageMapping("hello")
	Mono<String> hello(Mono<String> name);
}
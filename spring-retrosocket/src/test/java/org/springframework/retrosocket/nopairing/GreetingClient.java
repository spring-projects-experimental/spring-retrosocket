package org.springframework.retrosocket.nopairing;

import static org.springframework.retrosocket.RequesterMode.MANAGED;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retrosocket.RSocketClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@RSocketClient(host = "${greeting-client.host:localhost}", port = "${greeting-client.port:8888}", mode = MANAGED)
interface GreetingClient {

  @MessageMapping("greetings")
  Mono<GreetingResponse> greet();

  @MessageMapping("greetings-with-channel")
  Flux<GreetingResponse> greetParams(Flux<String> names);

  @MessageMapping("greetings-stream")
  Flux<GreetingResponse> greetStream(Mono<String> name);

  @MessageMapping("greetings-with-name")
  Mono<GreetingResponse> greet(Mono<String> name);

  @MessageMapping("fire-and-forget")
  Mono<Void> greetFireAndForget(Mono<String> name);

  @MessageMapping("greetings-mono-name.{name}.{age}")
  Mono<String> greetMonoNameDestinationVariable(@DestinationVariable("name") String name,
      @DestinationVariable("age") int age, @Payload Mono<String> payload);

}

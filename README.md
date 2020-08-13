# Spring Retrosocket


Spring Retrosocket aims to provide a Feign-like or Retrofit-like experience for declarative [RSocket](http://RSocket.io)-based clients.

## GitHub issues

We choose not to use GitHub issues for general usage questions and support, preferring to use issues solely for the tracking of bugs and enhancements. If you have a general usage question please do not open a [GitHub issue](http://github.com/spring-projects-experimental/spring-retrosocket), but use one of the other channels described below.

If you are reporting a bug, please help to speed up problem diagnosis by providing as
much information as possible. Ideally, that would include a small sample project that
reproduces the problem.

## Getting Started 

The easiest way might be to go to the Spring Initializr and generate a new project. Make sure that you specify the snapshot or milestone dependencies and then add the following to your build.

```xml
<dependency>
    <groupId>org.springframework.retrosocket</groupId>
    <artifactId>spring-retrosocket</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

In your Java code you need to enable the RSocket client support. Use the `@EnableRSocketClient` annotation on a `@Configuration` class. You’ll also need to define an `RSocketRequester` bean.

```java 
@SpringBootApplication
@EnableRSocketClient
class RSocketClientApplication {

 @Bean
 RSocketRequester requester(RSocketRequester.Builder builder) {
  return builder.connectTcp("localhost", 8888).block();
 }
}
```

Then, define an RSocket client interface, like this:

```java 
@RSocketClient
interface GreetingClient {

	@MessageMapping("supplier")
	Mono<GreetingResponse> greet();

	@MessageMapping("request-response")
	Mono<GreetingResponse> requestResponse(Mono<String> name);

	@MessageMapping("fire-and-forget")
	Mono<Void> fireAndForget(Mono<String> name);

	@MessageMapping("destination.variables.and.payload.annotations.{name}.{age}")
	Mono<String> greetMonoNameDestinationVariable(
            @DestinationVariable("name") String name,
	    @DestinationVariable("age") int age,
            @Payload Mono<String> payload);
}
```

You can then inject a reference to `GreetingClient` anywhere in your code and use it to issue requests of the RSocket-based service. 
If you invoke methods on this interface it’ll in turn issue requests to RSocket endpoints using the single configured `RSocketRequester` instance in the Spring context for you, 
turning destination variables into route variables, headers into metadata, and the payload into the data for the request.

## Documentation 

For more detailed information on this project, see
 [the documentation](https://repo.spring.io/snapshot/org/springframework/retrosocket/spring-retrosocket-docs/0.0.1-SNAPSHOT/spring-retrosocket-docs-0.0.1-20200813.045202-1.zip!/reference/index.html). 

## License

[Apache License v2.0](https://www.apache.org/licenses/LICENSE-2.0)
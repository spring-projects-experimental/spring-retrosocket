package com.joshlong.rsocket.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.core.ResolvableType;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
@RequiredArgsConstructor
class RSocketClientBuilder {

	private static Object[] findDestinationVariables(Object[] arguments, Parameter[] parameters) {
		List<Object> destinationVariableValues = new ArrayList<>();
		for (int i = 0; i < arguments.length; i++) {
			Parameter parameter = parameters[i];
			Object arg = arguments[i];
			if (parameter.getAnnotationsByType(DestinationVariable.class).length > 0) {
				destinationVariableValues.add(arg);
			}
		}
		return destinationVariableValues.toArray(new Object[0]);
	}

	private static Object findPayloadArgument(Object[] arguments, Parameter[] parameters) {
		Object payloadArgument = Mono.empty();
		if (arguments.length == 0) {
			payloadArgument = Mono.empty();
		}
		else if (arguments.length == 1) {
			payloadArgument = arguments[0];
		}
		else {
			Assert.isTrue(parameters.length == arguments.length,
					"there should be " + "an equal number of " + Parameter.class.getName() + " and objects");
			for (int i = 0; i < parameters.length; i++) {
				Parameter annotations = parameters[i];
				Object argument = arguments[i];
				if (annotations.getAnnotationsByType(Payload.class).length > 0) {
					payloadArgument = argument;
				}
			}
		}
		Assert.notNull(payloadArgument,
				"you must specify a @" + Payload.class.getName() + " parameter OR just one parameter");
		return payloadArgument;
	}

	private static Map<MimeType, Object> findCompositeMetadata(Object[] arguments, Parameter[] parameters) {
		Map<MimeType, Object> metadata = new HashMap<>();
		Assert.isTrue(parameters.length == arguments.length,
				"there should be an equal number of " + Parameter.class.getName() + " and objects");
		for (int i = 0; i < parameters.length; i++) {
			Parameter annotations = parameters[i];
			Object argument = arguments[i];
			Header[] headers = annotations.getAnnotationsByType(Header.class);
			if (headers.length > 0) {
				Header header = headers[0];
				Assert.state(StringUtils.hasText(header.value()) || StringUtils.hasText(header.name()),
						() -> "you can not use the @" + Header.class.getName()
								+ " annotation unless you provide a mimetype");
				MimeType mimeType = MimeTypeUtils.parseMimeType(header.value());
				metadata.put(mimeType, argument);
			}
		}
		return metadata;
	}

	public <T> T buildClientFor(Class<T> clazz, RSocketRequester rSocketRequester) {
		Assert.notNull(rSocketRequester, "the requester must not be null");
		Assert.notNull(clazz, "the Class must not be null");
		ProxyFactoryBean pfb = new ProxyFactoryBean();
		pfb.setTargetClass(clazz);
		pfb.addInterface(clazz);
		pfb.setAutodetectInterfaces(true);
		pfb.addAdvice((MethodInterceptor) methodInvocation -> {
			Method method = methodInvocation.getMethod();
			String methodName = method.getName();
			Class<?> returnType = method.getReturnType();
			Object[] arguments = methodInvocation.getArguments();
			Parameter[] parameters = method.getParameters();
			MessageMapping annotation = method.getAnnotation(MessageMapping.class);
			String route = annotation.value()[0];
			ResolvableType resolvableType = ResolvableType.forMethodReturnType(method);
			Class<?> rawClassForReturnType = resolvableType.getGenerics()[0].getRawClass();
			Object[] routeArguments = findDestinationVariables(arguments, parameters);
			Object payloadArgument = findPayloadArgument(arguments, parameters);
			Map<MimeType, Object> compositeMetadata = findCompositeMetadata(arguments, parameters);

			if (log.isDebugEnabled()) {
				log.debug("invoking " + methodName + " accepting " + arguments.length + " argument(s) for route "
						+ route + " with destination variables ("
						+ StringUtils.arrayToDelimitedString(routeArguments, ", ") + ")" + '.' + " The payload is "
						+ payloadArgument);
			}

			if (Mono.class.isAssignableFrom(returnType)) {
				// special case for fire-and-forget
				if (Void.class.isAssignableFrom(rawClassForReturnType)) {
					if (log.isDebugEnabled()) {
						log.debug("fire-and-forget");
					}
					return enrichMetadata(rSocketRequester.route(route, routeArguments), compositeMetadata)
							.data(payloadArgument)//
							.send();
				}
				else {
					if (log.isDebugEnabled()) {
						log.debug("request-response");
					}
					return enrichMetadata(rSocketRequester.route(route, routeArguments), compositeMetadata)//
							.data(payloadArgument)//
							.retrieveMono(rawClassForReturnType);
				}
			}

			if (Flux.class.isAssignableFrom(returnType)) {
				if (log.isDebugEnabled()) {
					log.debug("request-stream or channel");
				}
				return enrichMetadata(rSocketRequester.route(route, routeArguments), compositeMetadata)
						.data(payloadArgument).retrieveFlux(rawClassForReturnType);
			}
			// is there something more sensible to return?
			return Mono.empty();
		});

		return (T) pfb.getObject();
	}

	private RSocketRequester.RequestSpec enrichMetadata(RSocketRequester.RequestSpec route,
			Map<MimeType, Object> compositeMetadata) {
		if (!compositeMetadata.isEmpty()) {
			for (Map.Entry<MimeType, Object> entry : compositeMetadata.entrySet()) {
				route = route.metadata(entry.getValue(), entry.getKey());
			}
		}

		return route;
	}

}

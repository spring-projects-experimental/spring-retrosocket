package org.springframework.retrosocket.nativex;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanFactoryNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.retrosocket.EnableRSocketClients;
import org.springframework.retrosocket.RSocketClient;

/**
	* See https://github.com/spring-projects-experimental/spring-native/blob/main/spring-aot/src/main/java/org/springframework/data/RepositoryDefinitionConfigurationProcessor.java
	*
	* @author Josh Long
	*/
@Log4j2
public class RetrosocketBeanFactoryNativeConfigurationProcessor
	implements BeanFactoryNativeConfigurationProcessor {

	@Override
	@SneakyThrows
	public void process(ConfigurableListableBeanFactory beanFactory, NativeConfigurationRegistry registry) {

		Class<?>[] names = new Class<?>[]{
			Class.forName("org.springframework.retrosocket.RSocketClientsRegistrar"),
			Class.forName("org.springframework.retrosocket.RSocketClientFactoryBean"),
	  EnableRSocketClients.class , ReusableMessageFactory.class, MessageMapping.class, RSocketClient.class , DefaultFlowMessageFactory.class};

		for (Class<?> c : names) {
			registry
				.reflection()
				.forType(c)
				.withAccess(TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_METHODS, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS)
				.build();
		}

	}
}

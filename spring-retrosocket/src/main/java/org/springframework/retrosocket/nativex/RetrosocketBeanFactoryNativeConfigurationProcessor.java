package org.springframework.retrosocket.nativex;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanFactoryNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeProxyEntry;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.nativex.hint.TypeAccess;
import org.springframework.retrosocket.RSocketClient;

import java.util.Map;

/**
 * See
 * https://github.com/spring-projects-experimental/spring-native/blob/main/spring-aot/src/main/java/org/springframework/data/RepositoryDefinitionConfigurationProcessor.java
 *
 * @author Josh Long
 */
@Log4j2
public class RetrosocketBeanFactoryNativeConfigurationProcessor implements BeanFactoryNativeConfigurationProcessor {

	@Override
	@SneakyThrows
	public void process(ConfigurableListableBeanFactory beanFactory, NativeConfigurationRegistry registry) {
		Map<String, Object> beansWithAnnotation = beanFactory.getBeansWithAnnotation(RSocketClient.class);
		log.debug("there are " + beansWithAnnotation.size() + " beans with this annotation.");
		beansWithAnnotation.forEach((bn, e) -> {
			log.debug("found a bean of type " + e.getClass().getCanonicalName());
			registry.reflection().forType(e.getClass()).withAccess(TypeAccess.values()).build();

			for (Class<?> i : e.getClass().getInterfaces()) {
				registry.reflection().forType(i).withAccess(TypeAccess.values()).build();
				log.debug("registering the interface " + i.getName() + '.');
			}

			registry.reflection().forType(e.getClass()).withAccess(TypeAccess.values()).build();
			registry.proxy().add(NativeProxyEntry.ofInterfaces(e.getClass().getInterfaces()));
		});
	}

}

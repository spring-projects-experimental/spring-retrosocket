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
		log.info("there are " + beansWithAnnotation.size() + " beans with this annotation.");
		beansWithAnnotation.forEach((bn, e) -> {
			log.info("found a bean of type " + e.getClass().getCanonicalName());
			registry.reflection().forType(e.getClass()).withAccess(TypeAccess.values()).build();

			for (Class<?> i : e.getClass().getInterfaces()) {
				registry.reflection().forType(i).withAccess(TypeAccess.values()).build();
				log.info("registering the interface " + i.getName() + '.');
			}

			registry.reflection().forType(e.getClass()).withAccess(TypeAccess.values()).build();
			registry.proxy().add(NativeProxyEntry.ofInterfaces(e.getClass().getInterfaces()));
		});
		/*
		 * String[] beansWithAnnotation =
		 * beanFactory.getBeanNamesForAnnotation(RSocketClient.class); for (String bn :
		 * beansWithAnnotation) { BeanDefinition beanDefinition =
		 * beanFactory.getBeanDefinition(bn); String beanClassName =
		 * beanDefinition.getBeanClassName(); log.info("the bean class name is " +
		 * beanClassName); Class<?> clzz = Class.forName(beanClassName);
		 * registry.reflection().forType(clzz).withAccess(TypeAccess.values()).build();
		 * for (Class<?> c : clzz.getInterfaces()) log.info("interface: " + c.getName());
		 * registry.proxy().add(NativeProxyEntry.ofInterfaces(clzz.getInterfaces()));
		 *
		 * }
		 */
		// log.info("there are " + beansWithAnnotation.length + " beans with this
		// annotation.");
		/*
		 * beansWithAnnotation.forEach((bn, e) -> { log.info("found a bean of type " +
		 * e.getClass().getCanonicalName());
		 * registry.reflection().forType(e.getClass()).withAccess(TypeAccess.values()).
		 * build();
		 * registry.proxy().add(NativeProxyEntry.ofInterfaces(e.getClass().getInterfaces()
		 * )); });
		 */

		/*
		 *
		 * Class<?>[] names = new Class<?>[]{
		 * Class.forName("org.springframework.retrosocket.RSocketClientsRegistrar"),
		 * Class.forName("org.springframework.retrosocket.RSocketClientFactoryBean"),
		 * EnableRSocketClients.class, ReusableMessageFactory.class, MessageMapping.class,
		 * RSocketClient.class, DefaultFlowMessageFactory.class};
		 *
		 * for (Class<?> c : names) { registry .reflection() .forType(c)
		 * .withAccess(TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_METHODS,
		 * TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.DECLARED_FIELDS) .build(); }
		 *
		 * ///
		 *
		 *
		 * String[] beanNamesForAnnotation =
		 * beanFactory.getBeanNamesForAnnotation(RSocketClient.class); log.info
		 * ("there are " + beanNamesForAnnotation .length + " beans that RSocketClient") ;
		 * for (String bn : beanNamesForAnnotation) { BeanDefinition beanDefinition =
		 * beanFactory.getBeanDefinition(bn); String beanClassName =
		 * beanDefinition.getBeanClassName();
		 * log.info("registering proxy for beanClassName: " + beanClassName); registry
		 * .proxy() .add(ofInterfaceNames( beanClassName,
		 * "org.springframework.aop.SpringProxy",
		 * "org.springframework.aop.framework.Advised",
		 * "org.springframework.core.DecoratingProxy")); }
		 */

	}

}

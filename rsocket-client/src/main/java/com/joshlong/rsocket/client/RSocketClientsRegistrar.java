package com.joshlong.rsocket.client;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.*;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
class RSocketClientsRegistrar implements BeanFactoryPostProcessor, ImportBeanDefinitionRegistrar, BeanFactoryAware,
		EnvironmentAware, ResourceLoaderAware {

	private BeanFactory beanFactory;

	private Environment environment;

	private ResourceLoader resourceLoader;

	private Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(EnableRSocketClients.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();
		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {
		Collection<String> basePackages = getBasePackages(importingClassMetadata);
		if (log.isDebugEnabled()) {
			log.debug("scanning the following packages: "
					+ StringUtils.arrayToDelimitedString(basePackages.toArray(new String[0]), ", "));
		}
		ClassPathScanningCandidateComponentProvider scanner = this.buildScanner();
		basePackages.forEach(basePackage -> scanner.findCandidateComponents(basePackage)//
				.stream()//
				.filter(cc -> cc instanceof AnnotatedBeanDefinition)//
				.map(abd -> (AnnotatedBeanDefinition) abd)//
				.forEach(beanDefinition -> {
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
					this.validateInterface(annotationMetadata);
					this.registerRSocketClient(annotationMetadata, registry);
				}));
	}

	@SneakyThrows
	private void validateInterface(AnnotationMetadata annotationMetadata) {
		Assert.isTrue(annotationMetadata.isInterface(),
				"the @" + RSocketClient.class.getName() + " annotation must be used only on an interface");
		Class<?> clzz = Class.forName(annotationMetadata.getClassName());
		ReflectionUtils.doWithMethods(clzz, method -> {
			if (log.isDebugEnabled()) {
				log.debug("validating " + clzz.getName() + "#" + method.getName());
			}
			MessageMapping annotation = method.getAnnotation(MessageMapping.class);
			Assert.notNull(annotation, "you must use the @" + MessageMapping.class.getName()
					+ " annotation on every method on " + clzz.getName() + '.');
		});
	}

	private ClassPathScanningCandidateComponentProvider buildScanner() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false,
				this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition metadata) {
				return metadata.getMetadata().isIndependent() && !metadata.getMetadata().isAnnotation();
			}
		};
		scanner.addIncludeFilter(new AnnotationTypeFilter(RSocketClient.class));
		scanner.setResourceLoader(this.resourceLoader);
		return scanner;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
	}

	@SneakyThrows
	private void registerRSocketClient(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		String className = annotationMetadata.getClassName();
		if (log.isDebugEnabled()) {
			log.debug("trying to turn the interface " + className + " into an RSocketClientFactoryBean");
		}

		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(RSocketClientFactoryBean.class);
		definition.addPropertyValue("type", className);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
		beanDefinition.setPrimary(true);

		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[0]);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// log.info("postProcessBeanFactory");
		// why does this never get called?
	}

}

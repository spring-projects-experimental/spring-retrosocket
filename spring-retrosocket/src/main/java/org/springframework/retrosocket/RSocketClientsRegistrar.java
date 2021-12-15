package org.springframework.retrosocket;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.*;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 * @author Andy Clement
 */
@Log4j2
class RSocketClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

	private Environment environment;

	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry,
			BeanNameGenerator importBeanNameGenerator) {
		Collection<String> basePackages = this.getBasePackages(importingClassMetadata);
		ClassPathScanningCandidateComponentProvider scanner = this.buildScanner();
		basePackages.forEach(basePackage -> scanner.findCandidateComponents(basePackage)//
				.stream()//
				.filter(cc -> cc instanceof AnnotatedBeanDefinition)//
				.map(abd -> (AnnotatedBeanDefinition) abd)//
				.forEach(beanDefinition -> {
					AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
					boolean isClient = annotationMetadata.getAnnotationTypes().stream()
							.anyMatch(s -> s.equals(RSocketClient.class.getName()));
					if (!isClient)
						return;
					this.registerClient(annotationMetadata, registry);
				}));
	}

	@SneakyThrows
	private void registerClient(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
		String className = annotationMetadata.getClassName();
		log.info("going to build a client for " + className);
		Class<RSocketClientFactoryBean> rSocketClientFactoryBeanClass = RSocketClientFactoryBean.class;
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(rSocketClientFactoryBeanClass);
		definition.addPropertyValue("type", className);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);

		AbstractBeanDefinition beanDefinition = definition.getBeanDefinition();
		beanDefinition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, className);
		beanDefinition.setPrimary(true);
		beanDefinition.setBeanClass(rSocketClientFactoryBeanClass);
		BeanDefinitionHolder holder = new BeanDefinitionHolder(beanDefinition, className, new String[0]);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}

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

	private ClassPathScanningCandidateComponentProvider buildScanner() {
		ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false,
				this.environment) {

			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				return beanDefinition.getMetadata().isIndependent();
			}

			@Override
			protected boolean isCandidateComponent(MetadataReader metadataReader) {
				return !metadataReader.getClassMetadata().isAnnotation();
			}
		};
		scanner.addIncludeFilter(new AnnotationTypeFilter(RSocketClient.class));
		scanner.setResourceLoader(this.resourceLoader);
		return scanner;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

}

/*
 *
 * class Old {
 *
 *
 * @SneakyThrows private void validateInterface(AnnotationMetadata annotationMetadata) {
 * Assert.isTrue(annotationMetadata.isInterface(), "the @" + RSocketClient.class.getName()
 * + " annotation must be used only on an interface"); Class<?> clzz =
 * Class.forName(annotationMetadata.getClassName()); ReflectionUtils.doWithMethods(clzz,
 * method -> { log.info("validating " + clzz.getName() + "#" + method.getName());
 * MessageMapping annotation = method.getAnnotation(MessageMapping.class);
 * Assert.notNull(annotation, "you must use the @" + MessageMapping.class.getName() +
 * " annotation on every method on " + clzz.getName() + '.'); log.info(clzz.getName() +
 * " is a valid client"); }); } }
 */

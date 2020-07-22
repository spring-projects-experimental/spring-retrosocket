package com.joshlong.rsocket.client;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RSocketClientsRegistrar.class)
public @interface EnableRSocketClients {

	String[] value() default {};

	String[] basePackages() default {};

	Class<?>[] basePackageClasses() default {};

}

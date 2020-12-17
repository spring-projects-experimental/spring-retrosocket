package org.springframework.retrosocket;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RSocketClient {
  String  host() default "";
  String  port() default "";
  RequesterMode mode() default RequesterMode.PAIRING;
}

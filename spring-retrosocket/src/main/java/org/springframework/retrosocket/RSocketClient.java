package org.springframework.retrosocket;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.TYPE;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Target(TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Indexed
public @interface RSocketClient {

}

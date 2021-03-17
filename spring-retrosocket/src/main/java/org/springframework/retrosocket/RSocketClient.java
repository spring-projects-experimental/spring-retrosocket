package org.springframework.retrosocket;

import org.springframework.stereotype.Indexed;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Indexed
public @interface RSocketClient {

}

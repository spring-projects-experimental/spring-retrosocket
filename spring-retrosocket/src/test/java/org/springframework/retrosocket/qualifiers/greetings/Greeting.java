package org.springframework.retrosocket.qualifiers.greetings;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Greeting {

	private String message;

}

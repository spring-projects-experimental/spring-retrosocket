package org.springframework.retrosocket.nativex;

import org.springframework.nativex.type.*;

import java.util.Collections;
import java.util.List;

/**
 * Provides hints for Spring Native processing. This will need to register all the
 * interfaces with {@link io.rsocket.core.RSocketClient} as both proxy- and type-hints.
 *
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
public class RetrosocketNativeConfiguration implements NativeConfiguration {

	@Override
	public List<HintDeclaration> computeHints(TypeSystem typeSystem) {
		return Collections.emptyList();
	}

}

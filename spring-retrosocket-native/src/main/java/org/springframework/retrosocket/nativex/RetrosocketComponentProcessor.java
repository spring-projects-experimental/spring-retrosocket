package org.springframework.retrosocket.nativex;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.type.ComponentProcessor;
import org.springframework.nativex.type.NativeContext;
import org.springframework.nativex.type.Type;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

//
//@ProxyHint(
//	types = {
//		GreetingClient.class,
//		org.springframework.aop.SpringProxy.class,
//		org.springframework.aop.framework.Advised.class,
//		org.springframework.core.DecoratingProxy.class
//	}
//)
//@TypeHint(
//	typeNames = {
//		"org.springframework.retrosocket.RSocketClientsRegistrar",
//	},
//	types = {
//		Greeting.class,
//		GreetingClient.class,
//		ReusableMessageFactory.class,
//		DefaultFlowMessageFactory.class
//	})
//@ResourceHint(patterns = "nativex/GreetingClient.class")

/***
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
public class RetrosocketComponentProcessor implements ComponentProcessor {

	private static final String ANNOTATION_DN = "org.springframework.retrosocket.RSocketClient";

	private boolean ifIsRSocketClientInterface(NativeContext nativeContext, String candidateClassName,
			List<String> classifiers) {
		// Type type = nativeContext.getTypeSystem().resolveDotted(candidateClassName,
		// true);
		boolean hasRsocketClient = classifiers.stream()
				.anyMatch(annotationType -> annotationType.equals(ANNOTATION_DN));

		return hasRsocketClient;
	}

	@Override
	public boolean handle(NativeContext nativeContext, String candidateClassName, List<String> classifiers) {

		boolean handled = ifIsRSocketClientInterface(nativeContext, candidateClassName, classifiers);
		log.info("handle(context, " + candidateClassName + ", " + String.join(",", classifiers) + "): " + handled);
		return handled;
	}

	private final AtomicBoolean invariantsRegistered = new AtomicBoolean(false);

	protected void registerInvariants(NativeContext context, String className) {

		if (this.invariantsRegistered.get())
			return;

		String[] names = new String[] { "org.springframework.retrosocket.RSocketClientsRegistrar",
				"org.springframework.retrosocket.RSocketClientFactoryBean" };
		Class<?>[] types = new Class[] { ReusableMessageFactory.class, DefaultFlowMessageFactory.class };

		for (Class<?> n : types) {
			context.addReflectiveAccessHierarchy(n.getName(), AccessBits.ALL);
		}

		for (String n : names) {
			context.addReflectiveAccessHierarchy(n, AccessBits.ALL);
		}

		log.info("registering invariant types for " + className + ".");
		this.invariantsRegistered.set(true);
	}

	@Override
	public void process(NativeContext context, String candidateClassName, List<String> classifiers) {
		Type type = context.getTypeSystem().resolveDotted(candidateClassName, true);
		if (ifIsRSocketClientInterface(context, candidateClassName, classifiers)) {
			registerInvariants(context, candidateClassName);
			log.info("process(context, " + candidateClassName + ", " + String.join(",", classifiers) + "): "
					+ type.getDottedName() + ".");
			log.info("registering proxy and reflection for " + candidateClassName + '.');
			context.addProxy(candidateClassName, org.springframework.aop.SpringProxy.class.getName(),
					org.springframework.aop.framework.Advised.class.getName(),
					org.springframework.core.DecoratingProxy.class.getName());

			context.addReflectiveAccessHierarchy(type, AccessBits.ALL);
		}
	}

}

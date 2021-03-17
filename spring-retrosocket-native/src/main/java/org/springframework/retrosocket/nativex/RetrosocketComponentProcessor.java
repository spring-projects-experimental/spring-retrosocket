package org.springframework.retrosocket.nativex;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.message.DefaultFlowMessageFactory;
import org.apache.logging.log4j.message.ReusableMessageFactory;
import org.springframework.nativex.hint.AccessBits;
import org.springframework.nativex.type.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/***
	* @author <a href="mailto:josh@joshlong.com">Josh Long</a>
	* @author Andy Clement
	*/
@Log4j2
public class RetrosocketComponentProcessor implements ComponentProcessor {

	private static final String ANNOTATION_DN = "org.springframework.retrosocket.RSocketClient";

	private final AtomicBoolean invariantsRegistered = new AtomicBoolean(false);

	private boolean ifIsRSocketClientInterface(NativeContext nativeContext, String candidateClassName,
																																												List<String> classifiers) {
		return classifiers.stream().anyMatch(annotationType -> annotationType.equals(ANNOTATION_DN));
	}

	@Override
	public boolean handle(NativeContext nativeContext, String candidateClassName, List<String> classifiers) {
		Type resolvedComponentType = nativeContext.getTypeSystem().resolveDotted(candidateClassName, true);

		boolean handled = null != resolvedComponentType
			&& ifIsRSocketClientInterface(nativeContext, candidateClassName, classifiers);
		log.info("handle(context, " + candidateClassName + ", " + String.join(",", classifiers) + "): " + handled);
		return handled;
	}

	protected void registerInvariants(NativeContext context, String className) {

		if (this.invariantsRegistered.get())
			return;

		String[] names = new String[]{"org.springframework.retrosocket.RSocketClientsRegistrar",
			"org.springframework.retrosocket.RSocketClientFactoryBean"};
		Class<?>[] types = new Class[]{ReusableMessageFactory.class, DefaultFlowMessageFactory.class};

		for (Class<?> n : types) {
			context.addReflectiveAccessHierarchy(n.getName(), AccessBits.ALL);
		}

		for (String n : names) {
			context.addReflectiveAccessHierarchy(n, AccessBits.ALL);
		}

		log.debug("registering invariant types for " + className + ".");
		this.invariantsRegistered.set(true);
	}

	private final Set<String> added = new HashSet<>();

	@Override
	public void process(NativeContext context, String candidateClassName, List<String> classifiers) {

		registerInvariants(context, candidateClassName);

		Type type = context.getTypeSystem().resolveDotted(candidateClassName, true);
		if (ifIsRSocketClientInterface(context, candidateClassName, classifiers)) {

			log.info("process(context, " + candidateClassName + ", " + String.join(",", classifiers) + "): "
				+ type.getDottedName() + ".");
			log.info("registering proxy and reflection for " + candidateClassName + '.');
			context.addProxy(candidateClassName, org.springframework.aop.SpringProxy.class.getName(),
				org.springframework.aop.framework.Advised.class.getName(),
				org.springframework.core.DecoratingProxy.class.getName());
			context.addReflectiveAccessHierarchy(type, AccessBits.ALL);

			////// lifted shamelessly from WebComponentProcessor in Spring Native itself
			List<Method> mappings = type.getMethods(Method::isAtMapping);
			log.info("found " + mappings.size() + " mapping(s) for Retrosocket interface " + type .getDottedName() +
				": " + mappings);
			for (Method m : mappings) {
				List<Type> toProcess = new ArrayList<>();
				toProcess.addAll(m.getParameterTypes());
				toProcess.add(m.getReturnType());
				toProcess.addAll(m.getSignatureTypes(true));
				if (m.getReturnType() != null && m.getReturnType().getNestedTypes() != null) {
					toProcess.addAll(m.getReturnType().getNestedTypes());
				}

				for (Type t : toProcess) {
					String typename = t.getDottedName();
					if (ignore(typename)) {
						continue;
					}
					if (this.added.add(typename)) {
						Set<String> added = context.addReflectiveAccessHierarchy(typename,
							AccessBits.CLASS | AccessBits.DECLARED_METHODS | AccessBits.DECLARED_CONSTRUCTORS);
						analyze(context, type, added);
					}
				}
			}
		}
	}

	private void analyze(NativeContext imageContext, Type type, Set<String> added) {
		log.info("------------------------------------");
		log.info("analyze " + type.getDottedName());
	}

	private boolean ignore(String name) {
		return (name.startsWith("java.") || name.startsWith("org.springframework.ui.")
			|| name.startsWith("org.springframework.validation."));
	}

}

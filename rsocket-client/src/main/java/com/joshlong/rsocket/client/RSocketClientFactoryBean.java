package com.joshlong.rsocket.client;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * @author <a href="mailto:josh@joshlong.com">Josh Long</a>
 */
@Log4j2
class RSocketClientFactoryBean implements BeanFactoryAware, FactoryBean<Object> {

	private Class<?> type;

	private ListableBeanFactory context;

	private static RSocketRequester forInterface(Class<?> clientInterface, ListableBeanFactory context) {
		Map<String, RSocketRequester> rSocketRequestersInContext = context.getBeansOfType(RSocketRequester.class);
		int rSocketRequestersCount = rSocketRequestersInContext.size();
		Assert.state(rSocketRequestersCount > 0, () -> "there should be at least one "
				+ RSocketRequester.class.getName() + " in the context. Please consider defining one.");
		RSocketRequester rSocketRequester = null;
		Assert.notNull(clientInterface, "the client interface must be non-null");
		Assert.notNull(context, () -> "the " + ListableBeanFactory.class.getName() + " interface must be non-null");
		MergedAnnotation<Qualifier> qualifier = MergedAnnotations.from(clientInterface).get(Qualifier.class);
		if (qualifier.isPresent()) {
			String valueOfQualifierAnnotation = qualifier.getString(MergedAnnotation.VALUE);
			Map<String, RSocketRequester> beans = BeanFactoryAnnotationUtils.qualifiedBeansOfType(context,
					RSocketRequester.class, valueOfQualifierAnnotation);
			Assert.state(beans.size() == 1,
					() -> "I need just one " + RSocketRequester.class.getName() + " but I got " + beans.keySet());
			for (Map.Entry<String, RSocketRequester> entry : beans.entrySet()) {
				rSocketRequester = entry.getValue();
				if (log.isDebugEnabled()) {
					log.debug("found " + rSocketRequester + " with bean name " + entry.getKey() + " for @"
							+ RSocketClient.class.getName() + " interface " + clientInterface.getName() + '.');
				}
			}
		}
		else {
			Assert.state(rSocketRequestersCount == 1, () -> "there should be no more and no less than one unqualified "
					+ RSocketRequester.class.getName() + " instances in the context.");
			return rSocketRequestersInContext.values().iterator().next();
		}
		Assert.notNull(rSocketRequester, () -> "we could not find an " + RSocketRequester.class.getName()
				+ " for the @RSocketClient interface " + clientInterface.getName() + '.');
		return rSocketRequester;
	}

	@SneakyThrows
	public void setType(String type) {
		this.type = Class.forName(type);
	}

	@Override
	public Object getObject() {
		RSocketRequester rSocketRequester = forInterface(this.type, this.context);
		RSocketClientBuilder clientBuilder = this.context.getBean(RSocketClientBuilder.class);
		return clientBuilder.buildClientFor(this.type, rSocketRequester);
	}

	@Override
	public Class<?> getObjectType() {
		return this.type;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.state(beanFactory instanceof ListableBeanFactory,
				() -> "the BeanFactory is not an instance of a ListableBeanFactory");
		this.context = (ListableBeanFactory) beanFactory;
	}

}

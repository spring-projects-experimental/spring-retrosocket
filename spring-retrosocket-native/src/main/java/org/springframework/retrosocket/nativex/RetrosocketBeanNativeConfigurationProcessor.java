package org.springframework.retrosocket.nativex;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.aot.context.bootstrap.generator.bean.descriptor.BeanInstanceDescriptor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.BeanNativeConfigurationProcessor;
import org.springframework.aot.context.bootstrap.generator.infrastructure.nativex.NativeConfigurationRegistry;

/**
	* @author Josh Long
	*/
@Log4j2
public class RetrosocketBeanNativeConfigurationProcessor implements BeanNativeConfigurationProcessor {

	@SneakyThrows
	@Override
	public void process(BeanInstanceDescriptor descriptor, NativeConfigurationRegistry registry) {


	}
}

package com.bondarenko.ioc.testclasses.processor;

import com.bondarenko.ioc.entity.Bean;
import com.bondarenko.ioc.processor.BeanPostProcessor;
import org.junit.jupiter.api.Order;

import java.util.Map;

@Order(3)
public class TestBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(String beanName, Bean bean, Map<String, Bean> beanMap) {
        Object object = bean.getValue();
        if (bean.getValue().getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(5000);
            messageService.setProtocol("POP");
            object = messageService;
        }
        return object;
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Bean bean) {
        Object object = bean.getValue();
        if (bean.getValue().getClass().equals(MessageService.class)) {
            MessageService messageService = new MessageService();
            messageService.setPort(6000);
            object = messageService;
        }
        return object;
    }
}
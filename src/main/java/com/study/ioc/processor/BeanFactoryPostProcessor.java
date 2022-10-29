package com.study.ioc.processor;

import com.study.ioc.entity.BeanDefinition;

import java.util.Map;

public interface BeanFactoryPostProcessor {
    void postProcessBeanFactory(Map<String, BeanDefinition> beanDefinitionsMap);
}
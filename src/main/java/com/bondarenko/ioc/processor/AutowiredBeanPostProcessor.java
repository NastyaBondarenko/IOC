package com.bondarenko.ioc.processor;

import com.bondarenko.ioc.annotation.Autowired;
import com.bondarenko.ioc.annotation.Order;
import com.bondarenko.ioc.exception.NoUniqueBeanOfTypeException;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static java.lang.String.format;


@Order(Integer.MIN_VALUE)
public class AutowiredBeanPostProcessor implements BeanPostProcessor {

    private final Map<Class<?>, List<Object>> groupedBeansByClass = new HashMap<>();

    @Override
    public Object postProcessBeforeInitialization(String beanName, Object beanValue) {
        Arrays.stream(beanValue.getClass().getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowired.class))
                .forEach(field -> {
                    checkForMultipleBeans(field.getType(), beanName);
                    processFieldInjection(beanValue, field);
                });
        return beanValue;
    }

    private void checkForMultipleBeans(Class<?> type, String beanName) {
        Optional.ofNullable(groupedBeansByClass.get(type))
                .filter(list -> list.size() > 1)
                .ifPresent(list -> {
                    throw new NoUniqueBeanOfTypeException(format("Multiple beans of type %s in %s", type.getName(), beanName));
                });
    }

    private void processFieldInjection(Object beanValue, Field field) {
        groupedBeansByClass.getOrDefault(field.getType(), Collections.emptyList())
                .stream()
                .findFirst()
                .ifPresent(value -> findMethodToInjectRefDependencies(beanValue, field.getName(), value));
    }

    @Override
    public Object postProcessAfterInitialization(String beanName, Object beanValue) {
        return beanValue;
    }

    @SneakyThrows
    private void findMethodToInjectRefDependencies(Object beanValue, String fieldName, Object value) {
        Method[] methods = beanValue.getClass().getMethods();
        String methodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                method.invoke(beanValue, value);
            }
        }
    }
}
package com.study.ioc.context.impl;


import com.study.ioc.entity.Bean;
import com.study.ioc.entity.BeanDefinition;
import com.study.ioc.exception.BeanInstantiationException;
import com.study.ioc.exception.NoSuchBeanDefinitionException;
import com.study.ioc.exception.NoUniqueBeanOfTypeException;
import com.study.ioc.processor.BeanFactoryPostProcessor;
import com.study.ioc.processor.CustomBeanFactoryPostProcessor;
import com.study.ioc.service.MessageService;
import com.study.testclasses.DefaultUserService;
import com.study.testclasses.MailService;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AnnotationBasedApplicationContextTest {

    private AnnotationBasedApplicationContext annotationBasedApplicationContext;

    @Before
    public void before() {
        annotationBasedApplicationContext = new AnnotationBasedApplicationContext("com.study.testclasses");
    }

    @Test
    @DisplayName("should create beans successfully")
    public void shouldCreateBeansSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService = new BeanDefinition("MailService", "com.study.testclasses.MailService");
        beanDefinitionMap.put("MailService", beanDefinitionMailService);
        BeanDefinition beanDefinitionUserService = new BeanDefinition("DefaultUserService", "com.study.testclasses.DefaultUserService");
        beanDefinitionMap.put("DefaultUserService", beanDefinitionUserService);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);

        Bean actualMailBean = beanMap.get("MailService");
        assertEquals("MailService", actualMailBean.getId());
        assertEquals(MailService.class, actualMailBean.getValue().getClass());

        Bean actualUserBean = beanMap.get("DefaultUserService");
        assertNotNull(actualUserBean);
        assertEquals("DefaultUserService", actualUserBean.getId());
        assertEquals(DefaultUserService.class, actualUserBean.getValue().getClass());
    }

    @Test(expected = BeanInstantiationException.class)
    @DisplayName("should create beans with wrong class successfully")
    public void shouldCreateBeansWithWrongClassSuccessfully() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition errorBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.entity.TestClass");
        beanDefinitionMap.put("mailServicePOP", errorBeanDefinition);
        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
    }

    @Test
    @DisplayName("should get bean by id successfully")
    public void shouldGetBeanByIdSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        DefaultUserService beanValue2 = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        annotationBasedApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = (DefaultUserService) annotationBasedApplicationContext.getBean("bean1");
        DefaultUserService actualBeanValue2 = (DefaultUserService) annotationBasedApplicationContext.getBean("bean2");
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test
    @DisplayName("should get bean by clazz successfully")
    public void shouldGetBeanByClazzSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        MailService beanValue2 = new MailService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        annotationBasedApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = annotationBasedApplicationContext.getBean(DefaultUserService.class);
        MailService actualBeanValue2 = annotationBasedApplicationContext.getBean(MailService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test(expected = NoUniqueBeanOfTypeException.class)
    @DisplayName("should get bean by clazz no unique bean successfully")
    public void shouldGetBeanByClazzNoUniqueBeanSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean1", new Bean("bean1", new DefaultUserService()));
        beanMap.put("bean2", new Bean("bean2", new DefaultUserService()));
        annotationBasedApplicationContext.setBeans(beanMap);
        annotationBasedApplicationContext.getBean(DefaultUserService.class);
    }

    @Test
    @DisplayName("should get bean by id and clazz successfully")
    public void shouldGetBeanByIdAndClazzSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue1 = new DefaultUserService();
        DefaultUserService beanValue2 = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue1));
        beanMap.put("bean2", new Bean("bean2", beanValue2));
        annotationBasedApplicationContext.setBeans(beanMap);
        DefaultUserService actualBeanValue1 = annotationBasedApplicationContext.getBean("bean1", DefaultUserService.class);
        DefaultUserService actualBeanValue2 = annotationBasedApplicationContext.getBean("bean2", DefaultUserService.class);
        assertNotNull(actualBeanValue1);
        assertNotNull(actualBeanValue2);
        assertEquals(beanValue1, actualBeanValue1);
        assertEquals(beanValue2, actualBeanValue2);
    }

    @Test(expected = NoSuchBeanDefinitionException.class)
    @DisplayName("should get bean by id and clazz no such bean successfully")
    public void shouldGetBeanByIdAndClazzNoSuchBeanSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        DefaultUserService beanValue = new DefaultUserService();
        beanMap.put("bean1", new Bean("bean1", beanValue));
        annotationBasedApplicationContext.setBeans(beanMap);
        annotationBasedApplicationContext.getBean("bean1", MailService.class);
    }

    @Test
    @DisplayName("should get bean names successfully")
    public void shouldGetBeanNamesSuccessfully() {
        Map<String, Bean> beanMap = new HashMap<>();
        beanMap.put("bean3", new Bean("bean3", new DefaultUserService()));
        beanMap.put("bean4", new Bean("bean4", new DefaultUserService()));
        beanMap.put("bean5", new Bean("bean5", new DefaultUserService()));
        annotationBasedApplicationContext.setBeans(beanMap);
        List<String> actualBeansNames = annotationBasedApplicationContext.getBeanNames();
        List<String> expectedBeansNames = Arrays.asList("bean3", "bean4", "bean5");
        assertTrue(actualBeansNames.containsAll(expectedBeansNames));
        assertTrue(expectedBeansNames.containsAll(actualBeansNames));
    }

//    @Test
//    @DisplayName("should inject value dependencies successfully")
//    public void shouldInjectValueDependenciesSuccessfully() {
//        Map<String, Bean> beanMap = new HashMap<>();
//        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
//
//        MailService mailServicePOP = new MailService();
//        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
//        MailService mailServiceIMAP = new MailService();
//        beanMap.put("mailServiceIMAP", new Bean("mailServiceIMAP", mailServiceIMAP));
//
//        //  setPort(110) and setProtocol("POP3") via valueDependencies
//        BeanDefinition popServiceBeanDefinition = new BeanDefinition("mailServicePOP", "com.study.testclasses.MailService");
//        Map<String, String> popServiceValueDependencies = new HashMap<>();
//        popServiceValueDependencies.put("port", "110");
//        popServiceValueDependencies.put("protocol", "POP3");
//        popServiceBeanDefinition.setValueDependencies(popServiceValueDependencies);
//        beanDefinitionMap.put("mailServicePOP", popServiceBeanDefinition);
//
//        //  setPort(143) and setProtocol("IMAP") via valueDependencies
//        BeanDefinition imapServiceBeanDefinition = new BeanDefinition("mailServiceIMAP", "com.study.testclasses.MailService");
//        Map<String, String> imapServiceValueDependencies = new HashMap<>();
//        imapServiceValueDependencies.put("port", "143");
//        imapServiceValueDependencies.put("protocol", "IMAP");
//        imapServiceBeanDefinition.setValueDependencies(imapServiceValueDependencies);
//        beanDefinitionMap.put("mailServiceIMAP", imapServiceBeanDefinition);
//
//        annotationBasedApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);
//        assertEquals(110, mailServicePOP.getPort());
//        assertEquals("POP3", mailServicePOP.getProtocol());
//        assertEquals(143, mailServiceIMAP.getPort());
//        assertEquals("IMAP", mailServiceIMAP.getProtocol());
//    }

//    @Test
//    public void testInjectRefDependencies() {
//        Map<String, Bean> beanMap = new HashMap<>();
//        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
//
//        MailService mailServicePOP = new MailService();
//        mailServicePOP.setPort(110);
//        mailServicePOP.setProtocol("POP3");
//        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
//
//        DefaultUserService userService = new DefaultUserService();
//        beanMap.put("userService", new Bean("userService", userService));
//
//        //  setMailService(mailServicePOP) via refDependencies
//        BeanDefinition userServiceBeanDefinition = new BeanDefinition("userService", "com.study.entity.DefaultUserService");
//        Map<String, String> userServiceRefDependencies = new HashMap<>();
//        userServiceRefDependencies.put("mailService", "mailServicePOP");
//        userServiceBeanDefinition.setRefDependencies(userServiceRefDependencies);
//        beanDefinitionMap.put("userService", userServiceBeanDefinition);
//
//        annotationBasedApplicationContext.injectRefDependencies(beanDefinitionMap, beanMap);
//        assertNotNull(userService.getMailService());
//        assertEquals(110, ((MailService) userService.getMailService()).getPort());
//        assertEquals("POP3", ((MailService) userService.getMailService()).getProtocol());
//    }

    @Test
    public void testInjectValue() throws ReflectiveOperationException {
        MailService mailService = new MailService();
        Method setPortMethod = MailService.class.getDeclaredMethod("setPort", Integer.TYPE);
        annotationBasedApplicationContext.injectValue(mailService, setPortMethod, "465");
        int actualPort = mailService.getPort();
        assertEquals(465, actualPort);
    }

    @Test
    @DisplayName("test Create BeanPostProcessors")
    public void testCreateBeanPostProcessors() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.study.ioc.processor.CustomBeanFactoryPostProcessor");
        BeanDefinition beanDefinitionUserService =
                new BeanDefinition("userService", "com.study.entity.DefaultUserService");
        beanDefinitionMap.put("userService", beanDefinitionUserService);
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.study.ioc.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        Map<String, Bean> beanPostProcessors = annotationBasedApplicationContext.getBeanPostProcessorsMap();
        List<BeanFactoryPostProcessor> beanFactoryPostProcessors = annotationBasedApplicationContext.getBeanFactoryPostProcessors();

        assertNotNull(beanPostProcessors);
        assertNotNull(beanFactoryPostProcessors);
        assertEquals(CustomBeanFactoryPostProcessor.class, beanFactoryPostProcessors.get(0).getClass());
    }

    @Test
    @DisplayName("process BeanDefinitions")
    public void processBeanDefinitions() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
        BeanDefinition beanDefinitionMailService
                = new BeanDefinition("mailServicePOP", "com.study.testclasses.MailService");
        beanDefinitionMap.put("mailServicePOP", beanDefinitionMailService);

        BeanDefinition beanDefinitionUserService = new BeanDefinition("userService", "com.study.testclasses.DefaultUserService");
        beanDefinitionMap.put("userService", beanDefinitionUserService);

        BeanDefinition beanDefinitionFactoryPostProcessor =
                new BeanDefinition("beanFactoryPostProcessor", "com.study.ioc.processor.CustomBeanFactoryPostProcessor");
        beanDefinitionMap.put("beanFactoryPostProcessor", beanDefinitionFactoryPostProcessor);

        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeanDefinitions(beanDefinitionMap);
        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.injectValueDependencies(beanDefinitionMap, beanMap);

        MailService mailService = (MailService) beanMap.get("mailServicePOP").getValue();
        assertEquals(4500, mailService.getPort());
    }

    @Test
    @DisplayName("test Process Beans Before Initialization")
    public void testProcessBeansBeforeInitialization() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService = new BeanDefinition("messageService", "com.study.ioc.service.MessageService");
        beanDefinitionMap.put("messageService", beanDefinitionMessageService);
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.study.entity.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.study.ioc.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);

        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(5000, messageService.getPort());
        assertEquals("POP", messageService.getProtocol());
    }

//    @Test
//    @DisplayName("test Initialize Beans")
//    public void testInitializeBeans() {
//        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
//        BeanDefinition beanDefinitionMailService =
//                new BeanDefinition("mailServicePOP", "com.study.entity.MailService");
//        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
//        BeanDefinition beanDefinitionBeanPostProcessor =
//                new BeanDefinition("beanPostProcessor", "com.study.ioc.processor.CustomBeanPostProcessor");
//        beanDefinitionMap.put("beanPostProcessor", beanDefinitionBeanPostProcessor);
//
//        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
//        MailService mailServicePOP = new MailService();
//        mailServicePOP.setPort(110);
//        mailServicePOP.setProtocol("POP3");
//        beanMap.put("mailServicePOP", new Bean("mailServicePOP", mailServicePOP));
//
//        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
//        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);
//        annotationBasedApplicationContext.initializeBeans(beanMap);
//
//        assertEquals(4467, mailServicePOP.getPort());
//        assertEquals("IMAP", mailServicePOP.getProtocol());
//    }

    @Test
    @DisplayName("test Process Beans After Initialization")
    public void testProcessBeansAfterInitialization() {
        Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();

        BeanDefinition beanDefinitionMessageService = new BeanDefinition("messageService", "com.study.ioc.service.MessageService");
        beanDefinitionMap.put("messageService", beanDefinitionMessageService);
        BeanDefinition beanDefinitionMailService =
                new BeanDefinition("mailServiceIMAP", "com.study.entity.MailService");
        beanDefinitionMap.put("mailServiceIMAP", beanDefinitionMailService);
        BeanDefinition beanDefinitionPostProcessor =
                new BeanDefinition("beanPostProcessor", "com.study.ioc.processor.CustomBeanPostProcessor");
        beanDefinitionMap.put("beanPostProcessor", beanDefinitionPostProcessor);


        Map<String, Bean> beanMap = annotationBasedApplicationContext.createBeans(beanDefinitionMap);
        annotationBasedApplicationContext.createBeanPostProcessors(beanDefinitionMap);
        annotationBasedApplicationContext.processBeansBeforeInitialization(beanMap);
        annotationBasedApplicationContext.initializeBeans(beanMap);
        annotationBasedApplicationContext.processBeansAfterInitialization(beanMap);

        Bean actualMessageService = beanMap.get("messageService");
        MessageService messageService = (MessageService) actualMessageService.getValue();
        assertEquals(6000, messageService.getPort());
        assertEquals("IMAP", messageService.getProtocol());
    }
}

package com.vikmir.bpp;

import com.vikmir.annotation.Trimmed;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

public class TrimmedAnnotationBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Trimmed.class)) {
            return proxy(bean);
        }
        return bean;
    }

    private Object proxy(Object bean) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(bean.getClass());
        enhancer.setCallback(trim());
        return enhancer.create();
    }

    private MethodInterceptor trim() {
        return (obj, method, params, methodProxy) -> {
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof String arg) {
                    params[i] = arg.trim();
                }
            }
            var result = methodProxy.invokeSuper(obj, params);
            if (result instanceof String str) {
                result = str.trim();
            }
            return result;
        };
    }

}

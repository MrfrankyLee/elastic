package com.needayeah.elastic.common.utils;

import com.google.common.collect.Lists;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author lixiaole
 */
@Component
public class SpringApplicationHolder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static Map<Class<?>, Collection<?>> cache = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringApplicationHolder.applicationContext = applicationContext;
    }

    public static <T> T getBean(Class<T> clazz) {
        T t;
        Collection<?> list = cache.get(clazz);
        if (list == null) {
            t = applicationContext.getBean(clazz);
            cache.put(clazz, Lists.newArrayList(t));
            return t;
        }

        t = (T) list.iterator().next();
        return t;
    }

    public static <T> List<? extends T> getBeans(Class<T> clazz) {
        Collection<?> collection = cache.get(clazz);
        if (collection != null){
            ArrayList<T> ts = translate(collection);
            return ts;
        }

        Collection<T> values = applicationContext.getBeansOfType(clazz).values();
        cache.put(clazz, values);

        ArrayList<T> ts = translate(values);
        return ts;
    }

    private static <T> ArrayList<T> translate(Collection<?> collection) {
        ArrayList<T> ts = Lists.newArrayList();
        for (Object o : collection) {
            ts.add((T) o);
        }
        return ts;
    }

    /**
     * 获取对象
     *
     * @param name
     * @return Object
     * @throws BeansException
     */
    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}

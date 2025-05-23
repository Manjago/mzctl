package com.temnenkov.mzctl.di;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class SimpleDIContainer {
    private final Map<Class<?>, Object> beans = new HashMap<>();

    public <T> void registerBean(Class<T> type, T instance) {
        beans.put(type, instance);
    }

    public <T> T createBean(@NotNull Class<T> cls) {
        for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            boolean allParamsFound = true;

            for (int i = 0; i < paramTypes.length; i++) {
                Object paramInstance = beans.get(paramTypes[i]);
                if (paramInstance == null) {
                    allParamsFound = false;
                    break;
                }
                params[i] = paramInstance;
            }

            if (allParamsFound) {
                try {
                    T instance = cls.cast(constructor.newInstance(params));
                    registerBean(cls, instance);
                    return instance;
                } catch (Exception e) {
                    throw new SimpleDIException.BeanInstanceException("Не удалось создать bean: " + cls, e);
                }
            }
        }
        throw new SimpleDIException.ConstructorNotFoundException("Не найден подходящий конструктор для: " + cls);
    }

    public <T> T getBean(Class<T> type) {
        final Object bean = beans.get(type);
        if (bean == null) {
            throw new SimpleDIException.BeanNotFoundException("Bean не найден: " + type);
        }
        return type.cast(bean);
    }
}
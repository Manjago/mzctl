package com.temnenkov.mzctl.di;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SimpleDIContainerTest {

    private SimpleDIContainer container;

    @BeforeEach
    void setUp() {
        container = new SimpleDIContainer();
    }

    @DisplayName("Проверка регистрации и получения бина по интерфейсу")
    @Test
    void shouldRegisterAndRetrieveBeanByInterface() {
        // given
        MyService serviceImpl = new MyServiceImpl();
        container.registerBean(MyService.class, serviceImpl);

        // when
        MyService retrievedService = container.getBean(MyService.class);

        // then
        assertSame(serviceImpl, retrievedService, "Должен вернуть тот же экземпляр, что и зарегистрирован");
    }

    @DisplayName("Проверка автоматического создания бина с зависимостями")
    @Test
    void shouldAutomaticallyCreateBeanWithRegisteredDependencies() {
        // given
        DependencyA depA = new DependencyA();
        DependencyB depB = new DependencyB();
        container.registerBean(DependencyA.class, depA);
        container.registerBean(DependencyB.class, depB);

        // when
        BeanWithDependencies bean = container.createBean(BeanWithDependencies.class);

        // then
        assertNotNull(bean, "Созданный бин не должен быть null");
        assertSame(depA, bean.getDependencyA(), "DependencyA должна быть внедрена корректно");
        assertSame(depB, bean.getDependencyB(), "DependencyB должна быть внедрена корректно");
    }

    @DisplayName("Проверка ошибки при отсутствии зависимости")
    @Test
    void shouldThrowExceptionIfDependencyNotRegistered() {
        // given
        container.registerBean(DependencyA.class, new DependencyA());
        // DependencyB не зарегистрирована

        // when / then
        assertThrows(SimpleDIException.ConstructorNotFoundException.class,
                () -> container.createBean(BeanWithDependencies.class), "Должен выбросить исключение, если " +
                        "зависимость не зарегистрирована");
    }

    @DisplayName("Проверка ошибки при попытке получить незарегистрированный бин")
    @Test
    void shouldThrowExceptionIfBeanNotRegistered() {
        assertThrows(SimpleDIException.BeanNotFoundException.class, () -> container.getBean(MyService.class), "Должен" +
                " выбросить исключение, если бин не зарегистрирован");
    }

    @DisplayName("Проверка, что при создании бина автоматически регистрируешь его в контейнере")
    @Test
    void shouldAutomaticallyRegisterBeanAfterCreation() {
        // given
        DependencyA depA = new DependencyA();
        DependencyB depB = new DependencyB();
        container.registerBean(DependencyA.class, depA);
        container.registerBean(DependencyB.class, depB);

        // when
        BeanWithDependencies createdBean = container.createBean(BeanWithDependencies.class);
        BeanWithDependencies retrievedBean = container.getBean(BeanWithDependencies.class);

        // then
        assertSame(createdBean, retrievedBean, "Созданный бин должен автоматически регистрироваться в контейнере");
    }

    interface MyService {
        void doSomething();
    }

    static class MyServiceImpl implements MyService {
        @Override
        public void doSomething() {
        }
    }

    static class DependencyA {
    }

    static class DependencyB {
    }

    static class BeanWithDependencies {
        private final DependencyA dependencyA;
        private final DependencyB dependencyB;

        public BeanWithDependencies(DependencyA dependencyA, DependencyB dependencyB) {
            this.dependencyA = dependencyA;
            this.dependencyB = dependencyB;
        }

        public DependencyA getDependencyA() {
            return dependencyA;
        }

        public DependencyB getDependencyB() {
            return dependencyB;
        }
    }
}


package by.ralovets.majector.service.impl;

import by.ralovets.majector.data.correct.*;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsClass01;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsClass02;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsInterface;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorClass01;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorClass02;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorInterface;
import by.ralovets.majector.data.incorrect.recursion.doubl.DoubleRecursionA;
import by.ralovets.majector.data.incorrect.recursion.doubl.DoubleRecursionB;
import by.ralovets.majector.data.incorrect.recursion.multiple.MultipleRecursionA;
import by.ralovets.majector.data.incorrect.recursion.multiple.MultipleRecursionB;
import by.ralovets.majector.data.incorrect.recursion.multiple.MultipleRecursionC;
import by.ralovets.majector.data.incorrect.recursion.single.SingleRecursionA;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.RecursiveInjectionException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.service.Injector;
import by.ralovets.majector.service.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class InjectorImplTest {

    static Stream<Arguments> twoClasses_recursive() {
        return Stream.of(
                arguments(SingleRecursionA.class, SingleRecursionA.class),
                arguments(DoubleRecursionA.class, DoubleRecursionA.class),
                arguments(MultipleRecursionA.class, MultipleRecursionA.class)
        );
    }

    static Stream<Arguments> twoClasses_with_null() {
        return Stream.of(
                arguments(null, Object.class),
                arguments(Object.class, null),
                arguments(null, null)
        );
    }

    static Stream<Arguments> twoClasses_too_many_constructors() {
        return Stream.of(
                arguments(ToManyConstructorsInterface.class, ToManyConstructorsClass01.class),
                arguments(ToManyConstructorsInterface.class, ToManyConstructorsClass02.class)
        );
    }

    static Stream<Arguments> twoClasses_with_no_constructors() {
        return Stream.of(
                arguments(NoneConstructorInterface.class, NoneConstructorClass01.class),
                arguments(NoneConstructorInterface.class, NoneConstructorClass02.class)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_null")
    public <T> void bind_with_null(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                IllegalArgumentException.class,
                () -> injector.bind(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_too_many_constructors")
    public <T> void bind_too_many_constructors(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                TooManyConstructorsException.class,
                () -> injector.bind(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_no_constructors")
    public <T> void bind_with_no_constructors(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                ConstructorNotFoundException.class,
                () -> injector.bind(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_null")
    public <T> void bindSingleton_with_null(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                IllegalArgumentException.class,
                () -> injector.bindSingleton(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_too_many_constructors")
    public <T> void bindSingleton_too_many_constructors(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                TooManyConstructorsException.class,
                () -> injector.bindSingleton(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_no_constructors")
    public <T> void bindSingleton_with_no_constructors(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        assertThrows(
                ConstructorNotFoundException.class,
                () -> injector.bindSingleton(intf, impl)
        );
    }

    @Test
    public void getProvider_with_null() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new InjectorImpl().getProvider(null)
        );
    }

    @Test
    public void getProvider_for_prototypes() {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        injector.bind(EventDao.class, InMemoryEventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);
        injector.bind(RestEventController.class, RestEventController.class);

        Provider<?> daoProvider = injector.getProvider(EventDao.class);
        assertNotNull(daoProvider);
        assertNotNull(daoProvider.getInstance());
        assertNotSame(daoProvider.getInstance(), daoProvider.getInstance());

        Provider<?> serviceProvider = injector.getProvider(EventService.class);
        assertNotNull(serviceProvider);
        assertNotNull(serviceProvider.getInstance());
        assertNotSame(serviceProvider.getInstance(), serviceProvider.getInstance());

        Provider<?> controllerProvider = injector.getProvider(RestEventController.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertNotSame(controllerProvider.getInstance(), controllerProvider.getInstance());
    }

    @Test
    public void getProvider_for_singletons() {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        injector.bindSingleton(EventDao.class, InMemoryEventDAOImpl.class);
        injector.bindSingleton(EventService.class, EventServiceImpl.class);
        injector.bind(RestEventController.class, RestEventController.class);

        Provider<?> daoProvider = injector.getProvider(EventDao.class);
        assertNotNull(daoProvider);
        assertNotNull(daoProvider.getInstance());
        assertSame(daoProvider.getInstance(), daoProvider.getInstance());

        Provider<?> serviceProvider = injector.getProvider(EventService.class);
        assertNotNull(serviceProvider);
        assertNotNull(serviceProvider.getInstance());
        assertSame(serviceProvider.getInstance(), serviceProvider.getInstance());

        Provider<?> controllerProvider = injector.getProvider(RestEventController.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertNotSame(controllerProvider.getInstance(), controllerProvider.getInstance());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_recursive")
    public <T> void getProvider_for_singletons_recursive(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();
        assertNotNull(injector);

        injector.bindSingleton(SingleRecursionA.class, SingleRecursionA.class);
        injector.bindSingleton(DoubleRecursionA.class, DoubleRecursionA.class);
        injector.bindSingleton(DoubleRecursionB.class, DoubleRecursionB.class);
        injector.bindSingleton(MultipleRecursionA.class, MultipleRecursionA.class);
        injector.bindSingleton(MultipleRecursionB.class, MultipleRecursionB.class);
        injector.bindSingleton(MultipleRecursionC.class, MultipleRecursionC.class);

        assertThrows(
                RecursiveInjectionException.class,
                () -> injector.getProvider(intf)
        );
    }
}
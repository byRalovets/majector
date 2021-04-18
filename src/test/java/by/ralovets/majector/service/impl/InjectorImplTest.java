package by.ralovets.majector.service.impl;

import by.ralovets.majector.dataset.correct.*;
import by.ralovets.majector.dataset.constructor.toomany.TooManyConstructorsClass01;
import by.ralovets.majector.dataset.constructor.toomany.TooManyConstructorsClass02;
import by.ralovets.majector.dataset.constructor.toomany.ToManyConstructorsInterface;
import by.ralovets.majector.dataset.constructor.notfound.ConstructorNotFoundClass01;
import by.ralovets.majector.dataset.constructor.notfound.ConstructorNotFoundClass02;
import by.ralovets.majector.dataset.constructor.notfound.ConstructorNotFoundInterface;
import by.ralovets.majector.dataset.recursion.doubl.DoubleRecursionA;
import by.ralovets.majector.dataset.recursion.doubl.DoubleRecursionB;
import by.ralovets.majector.dataset.recursion.multiple.MultipleRecursionA;
import by.ralovets.majector.dataset.recursion.multiple.MultipleRecursionB;
import by.ralovets.majector.dataset.recursion.multiple.MultipleRecursionC;
import by.ralovets.majector.dataset.recursion.single.SingleRecursionA;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.RecursiveInjectionException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.service.Injector;
import by.ralovets.majector.service.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class InjectorImplTest {

    private static Stream<Arguments> twoClassesWithException() {
        return Stream.of(
                arguments(IllegalArgumentException.class, null, Object.class),
                arguments(IllegalArgumentException.class, Object.class, null),
                arguments(IllegalArgumentException.class, null, null),
                arguments(TooManyConstructorsException.class, ToManyConstructorsInterface.class, TooManyConstructorsClass01.class),
                arguments(TooManyConstructorsException.class, ToManyConstructorsInterface.class, TooManyConstructorsClass02.class),
                arguments(ConstructorNotFoundException.class, ConstructorNotFoundInterface.class, ConstructorNotFoundClass01.class),
                arguments(ConstructorNotFoundException.class, ConstructorNotFoundInterface.class, ConstructorNotFoundClass02.class)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClassesWithException")
    public <T> void bindWithException(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new InjectorImpl().bind(intf, impl));
    }

    @ParameterizedTest
    @MethodSource("twoClassesWithException")
    public <T> void bindSingletonWithException(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new InjectorImpl().bindSingleton(intf, impl));
    }

    @Test
    public void getProviderWithNull() {
        assertThrows(IllegalArgumentException.class, () -> new InjectorImpl().getProvider(null));
    }

    @Test
    public void getProviderForPrototypes() {
        Injector injector = new InjectorImpl();

        injector.bind(EventDao.class, InMemoryEventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);
        injector.bind(RestEventController.class, RestEventController.class);

        Provider<?> controllerProvider = injector.getProvider(RestEventController.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertNotSame(controllerProvider.getInstance(), controllerProvider.getInstance());
    }

    @Test
    public void getProviderForSingletons() {
        Injector injector = new InjectorImpl();

        injector.bindSingleton(EventDao.class, InMemoryEventDAOImpl.class);
        injector.bindSingleton(EventService.class, EventServiceImpl.class);
        injector.bindSingleton(RestEventController.class, RestEventController.class);

        Provider<?> controllerProvider = injector.getProvider(RestEventController.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertSame(controllerProvider.getInstance(), controllerProvider.getInstance());
    }

    @ParameterizedTest
    @ValueSource(classes = {SingleRecursionA.class, DoubleRecursionA.class, MultipleRecursionA.class})
    public <T> void getProviderForSingletonsWithRecursion(Class<T> intf) {
        Injector injector = new InjectorImpl();

        injector.bindSingleton(SingleRecursionA.class, SingleRecursionA.class);
        injector.bindSingleton(DoubleRecursionA.class, DoubleRecursionA.class);
        injector.bindSingleton(DoubleRecursionB.class, DoubleRecursionB.class);
        injector.bindSingleton(MultipleRecursionA.class, MultipleRecursionA.class);
        injector.bindSingleton(MultipleRecursionB.class, MultipleRecursionB.class);
        injector.bindSingleton(MultipleRecursionC.class, MultipleRecursionC.class);

        assertThrows(RecursiveInjectionException.class, () -> injector.getProvider(intf));
    }

    @ParameterizedTest
    @ValueSource(classes = {SingleRecursionA.class, DoubleRecursionA.class, MultipleRecursionA.class})
    public <T> void getProviderForPrototypesWithRecursion(Class<T> intf) {
        Injector injector = new InjectorImpl();

        injector.bind(SingleRecursionA.class, SingleRecursionA.class);
        injector.bind(DoubleRecursionA.class, DoubleRecursionA.class);
        injector.bind(DoubleRecursionB.class, DoubleRecursionB.class);
        injector.bind(MultipleRecursionA.class, MultipleRecursionA.class);
        injector.bind(MultipleRecursionB.class, MultipleRecursionB.class);
        injector.bind(MultipleRecursionC.class, MultipleRecursionC.class);

        assertThrows(RecursiveInjectionException.class, () -> injector.getProvider(intf));
    }
}
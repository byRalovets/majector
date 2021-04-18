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

    private static Stream<Arguments> twoClasses_exception() {
        return Stream.of(
                arguments(IllegalArgumentException.class, null, Object.class),
                arguments(IllegalArgumentException.class, Object.class, null),
                arguments(IllegalArgumentException.class, null, null),
                arguments(TooManyConstructorsException.class, ToManyConstructorsInterface.class, ToManyConstructorsClass01.class),
                arguments(TooManyConstructorsException.class, ToManyConstructorsInterface.class, ToManyConstructorsClass02.class),
                arguments(ConstructorNotFoundException.class, NoneConstructorInterface.class, NoneConstructorClass01.class),
                arguments(ConstructorNotFoundException.class, NoneConstructorInterface.class, NoneConstructorClass02.class)
        );
    }

    private static Stream<Arguments> twoClasses_recursion() {
        return Stream.of(
                arguments(SingleRecursionA.class, SingleRecursionA.class),
                arguments(DoubleRecursionA.class, DoubleRecursionA.class),
                arguments(MultipleRecursionA.class, MultipleRecursionA.class)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_exception")
    public <T> void bind_exception(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new InjectorImpl().bind(intf, impl));
    }

    @ParameterizedTest
    @MethodSource("twoClasses_exception")
    public <T> void bindSingleton_exception(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new InjectorImpl().bindSingleton(intf, impl));
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

        injector.bind(EventDao.class, InMemoryEventDAOImpl.class);
        injector.bind(EventService.class, EventServiceImpl.class);
        injector.bind(RestEventController.class, RestEventController.class);

        Provider<?> controllerProvider = injector.getProvider(RestEventController.class);
        assertNotNull(controllerProvider);
        assertNotNull(controllerProvider.getInstance());
        assertNotSame(controllerProvider.getInstance(), controllerProvider.getInstance());
    }

    @Test
    public void getProvider_for_singletons() {
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
    @MethodSource("twoClasses_recursion")
    public <T> void getProvider_for_singletons_recursive(Class<T> intf, Class<? extends T> impl) {
        Injector injector = new InjectorImpl();

        injector.bindSingleton(SingleRecursionA.class, SingleRecursionA.class);
        injector.bindSingleton(DoubleRecursionA.class, DoubleRecursionA.class);
        injector.bindSingleton(DoubleRecursionB.class, DoubleRecursionB.class);
        injector.bindSingleton(MultipleRecursionA.class, MultipleRecursionA.class);
        injector.bindSingleton(MultipleRecursionB.class, MultipleRecursionB.class);
        injector.bindSingleton(MultipleRecursionC.class, MultipleRecursionC.class);

        assertThrows(RecursiveInjectionException.class, () -> injector.getProvider(intf));
    }
}
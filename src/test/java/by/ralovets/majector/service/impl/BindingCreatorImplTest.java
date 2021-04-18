package by.ralovets.majector.service.impl;

import by.ralovets.majector.data.correct.EventDao;
import by.ralovets.majector.data.correct.EventService;
import by.ralovets.majector.data.correct.EventServiceImpl;
import by.ralovets.majector.data.correct.InMemoryEventDAOImpl;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsClass01;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsClass02;
import by.ralovets.majector.data.incorrect.constructor.many.ToManyConstructorsInterface;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorClass01;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorClass02;
import by.ralovets.majector.data.incorrect.constructor.none.NoneConstructorInterface;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.model.Binding;
import by.ralovets.majector.model.Scope;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BindingCreatorImplTest {

    private static Stream<Arguments> twoClasses_correct() {
        return Stream.of(
                arguments(EventDao.class, InMemoryEventDAOImpl.class),
                arguments(EventService.class, EventServiceImpl.class)
        );
    }

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

    @ParameterizedTest
    @MethodSource("twoClasses_correct")
    <T> void getBinding(Class<T> intf, Class<? extends T> impl) {
        Binding binding = new BindingCreatorImpl().getBinding(intf, impl);
        assertNotNull(binding);

        assertSame(intf, binding.getIntfType());
        assertSame(impl, binding.getImplType());
        assertEquals(impl.getConstructors()[0], binding.getConstructor());
        assertArrayEquals(impl.getConstructors()[0].getParameterTypes(), binding.getConstructor().getParameterTypes());
        assertEquals(Scope.PROTOTYPE, binding.getScope());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_exception")
    <T> void getBinding_exception(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new BindingCreatorImpl().getBinding(intf, impl));
    }

    @ParameterizedTest
    @MethodSource("twoClasses_correct")
    <T> void getSingletonBinding(Class<T> intf, Class<? extends T> impl) {
        Binding binding = new BindingCreatorImpl().getSingletonBinding(intf, impl);
        assertNotNull(binding);

        assertSame(intf, binding.getIntfType());
        assertSame(impl, binding.getImplType());
        assertEquals(impl.getConstructors()[0], binding.getConstructor());
        assertArrayEquals(impl.getConstructors()[0].getParameterTypes(), binding.getConstructor().getParameterTypes());
        assertEquals(Scope.SINGLETON, binding.getScope());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_exception")
    <T> void getSingletonBinding_exception(Class<? extends Exception> e, Class<T> intf, Class<? extends T> impl) {
        assertThrows(e, () -> new BindingCreatorImpl().getSingletonBinding(intf, impl));
    }
}
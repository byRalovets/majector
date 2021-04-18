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
import by.ralovets.majector.model.ClassBinding;
import by.ralovets.majector.service.ClassBindingCreator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class ClassBindingCreatorImplTest {

    static Stream<Arguments> twoClasses_correct() {
        return Stream.of(
                arguments(EventDao.class, InMemoryEventDAOImpl.class),
                arguments(EventService.class, EventServiceImpl.class)
        );
    }

    static Stream<Arguments> twoClasses_with_null() {
        return Stream.of(
                arguments(EventDao.class, null),
                arguments(null, EventServiceImpl.class),
                arguments(EventService.class, null),
                arguments(null, EventServiceImpl.class),
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

    @Test
    void getInstance() {
        assertNotNull(ClassBindingCreatorImpl.getInstance());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_correct")
    <T> void getBinding(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        ClassBinding binding = classBindingCreator.getBinding(intf, impl);
        assertNotNull(binding);

        assertSame(intf, binding.getIntfType());
        assertSame(impl, binding.getImplType());
        assertEquals(impl.getConstructors()[0], binding.getInjectedConstructor());
        assertArrayEquals(impl.getConstructors()[0].getParameterTypes(), binding.getConstructorArgsTypes());
        assertFalse(binding.isSingleton());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_null")
    <T> void getBinding_with_null(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                IllegalArgumentException.class,
                () -> classBindingCreator.getBinding(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_too_many_constructors")
    <T> void getBinding_too_many_constructors(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                TooManyConstructorsException.class,
                () -> classBindingCreator.getBinding(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_no_constructors")
    <T> void getBinding_with_no_constructors(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                ConstructorNotFoundException.class,
                () -> classBindingCreator.getBinding(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_correct")
    <T> void getSingletonBinding(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        ClassBinding binding = classBindingCreator.getSingletonBinding(intf, impl);
        assertNotNull(binding);

        assertSame(intf, binding.getIntfType());
        assertSame(impl, binding.getImplType());
        assertEquals(impl.getConstructors()[0], binding.getInjectedConstructor());
        assertArrayEquals(impl.getConstructors()[0].getParameterTypes(), binding.getConstructorArgsTypes());
        assertTrue(binding.isSingleton());
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_null")
    <T> void getSingletonBinding_with_null(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                IllegalArgumentException.class,
                () -> classBindingCreator.getSingletonBinding(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_too_many_constructors")
    <T> void getSingletonBinding_too_many_constructors(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                TooManyConstructorsException.class,
                () -> classBindingCreator.getSingletonBinding(intf, impl)
        );
    }

    @ParameterizedTest
    @MethodSource("twoClasses_with_no_constructors")
    <T> void getSingletonBinding_with_no_constructors(Class<T> intf, Class<? extends T> impl) {
        ClassBindingCreator classBindingCreator = ClassBindingCreatorImpl.getInstance();
        assertNotNull(classBindingCreator);

        assertThrows(
                ConstructorNotFoundException.class,
                () -> classBindingCreator.getSingletonBinding(intf, impl)
        );
    }
}
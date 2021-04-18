package by.ralovets.majector.service.impl;

import by.ralovets.majector.annotation.Inject;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.model.ClassBinding;
import by.ralovets.majector.service.ClassBindingCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import static java.util.Objects.isNull;

public class ClassBindingCreatorImpl implements ClassBindingCreator {

    private final static Class<? extends Annotation> ANNOTATION_CLASS = Inject.class;
    private final static ClassBindingCreatorImpl instance = new ClassBindingCreatorImpl();

    private ClassBindingCreatorImpl() {
    }

    public static ClassBindingCreatorImpl getInstance() {
        return instance;
    }

    @Override
    public <T> ClassBinding getBinding(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        Constructor<?> usedConstructor;
        Constructor<?>[] annotatedConstructors = getAnnotatedConstructors(impl);

        if (annotatedConstructors.length > 1) {
            throw new TooManyConstructorsException();
        }

        if (annotatedConstructors.length == 0) {
            usedConstructor = getDefaultConstructor(impl);
            if (isNull(usedConstructor)) {
                throw new ConstructorNotFoundException();
            }
        } else {
            usedConstructor = annotatedConstructors[0];
        }

        Class<?>[] argsTypes = usedConstructor.getParameterTypes();

        return new ClassBinding(intf, impl, usedConstructor, argsTypes, false);
    }

    @Override
    public <T> ClassBinding getSingletonBinding(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        ClassBinding binding = getBinding(intf, impl);
        binding.setSingleton(true);
        return binding;
    }

    private Constructor<?> getDefaultConstructor(Class<?> clazz) {
        for (Constructor<?> c : clazz.getConstructors()) {
            if (c.getParameterCount() == 0) {
                return c;
            }
        }
        return null;
    }

    private Constructor<?>[] getAnnotatedConstructors(Class<?> clazz) {
        return Arrays.stream(clazz.getConstructors())
                .filter(c -> c.isAnnotationPresent(ANNOTATION_CLASS))
                .toArray(Constructor<?>[]::new);
    }
}

package by.ralovets.majector.service.impl;

import by.ralovets.majector.annotation.Inject;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.model.Binding;
import by.ralovets.majector.model.Scope;
import by.ralovets.majector.service.BindingCreator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import static java.util.Objects.isNull;

public class BindingCreatorImpl implements BindingCreator {

    private static final Class<? extends Annotation> ANNOTATION_CLASS = Inject.class;

    public BindingCreatorImpl() {
    }

    /**
     * Creates binding of interface to implementation.
     *
     * @throws IllegalArgumentException     - if one of the arguments is null
     * @throws TooManyConstructorsException - if there are more than two constructors with @Inject
     * @throws ConstructorNotFoundException - if there is no constructors (with @Inject or default)
     */
    @Override
    public <T> Binding getBinding(Class<T> intf, Class<? extends T> impl) {
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

        return new Binding(intf, impl, usedConstructor, Scope.PROTOTYPE);
    }

    /**
     * Creates binding of interface to implementation for singletons.
     *
     * @throws IllegalArgumentException     - if one of the arguments is null
     * @throws TooManyConstructorsException - if there are more than two constructors with @Inject
     * @throws ConstructorNotFoundException - if there is no constructors (with @Inject or default)
     */
    @Override
    public <T> Binding getSingletonBinding(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        Binding binding = getBinding(intf, impl);
        binding.setScope(Scope.SINGLETON);
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

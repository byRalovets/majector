package by.ralovets.majector.service.impl;

import by.ralovets.majector.annotation.Inject;
import by.ralovets.majector.exception.ConstructorNotFoundException;
import by.ralovets.majector.exception.TooManyConstructorsException;
import by.ralovets.majector.model.ClassBinding;
import by.ralovets.majector.service.ClassBindingCreator;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static java.util.Objects.isNull;

public class ClassBindingCreatorImpl implements ClassBindingCreator {

    private final static ClassBindingCreatorImpl instance = new ClassBindingCreatorImpl();

    public static ClassBindingCreatorImpl getInstance() {
        return instance;
    }

    @Override
    public <T> ClassBinding getBinding(Class<T> intf, Class<? extends T> impl) {
        Constructor<?>[] constructors = impl.getConstructors();
        Constructor<?>[] annotatedConstructors = getAnnotatedConstructors(constructors);
        Constructor<?> usedConstructor;

        if (annotatedConstructors.length > 1) {
            throw new TooManyConstructorsException();
        }

        if (annotatedConstructors.length == 0) {
            usedConstructor = getDefaultConstructor(constructors);
            if (isNull(usedConstructor))
                throw new ConstructorNotFoundException();
        } else {
            usedConstructor = annotatedConstructors[0];
        }

        Class<?>[] argsTypes = usedConstructor.getParameterTypes();

        return new ClassBinding(false, intf, impl, argsTypes, usedConstructor);
    }

    @Override
    public <T> ClassBinding getSingletonBinding(Class<T> intf, Class<? extends T> impl) {
        ClassBinding binding = getBinding(intf, impl);
        binding.setSingleton(true);
        return binding;
    }

    private Constructor<?> getDefaultConstructor(Constructor<?>[] constructors) {
        for (Constructor<?> c : constructors) {
            if (c.getParameterCount() == 0) {
                return c;
            }
        }
        return null;
    }

    private Constructor<?>[] getAnnotatedConstructors(Constructor<?>[] constructors) {
        return Arrays.stream(constructors)
                .filter(c -> c.isAnnotationPresent(Inject.class)).toArray(Constructor<?>[]::new);
    }

    private ClassBindingCreatorImpl() {
    }
}

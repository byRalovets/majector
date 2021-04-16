package by.ralovets.majector.service.impl;

import by.ralovets.majector.model.ClassBinding;
import by.ralovets.majector.service.ClassBindingCreator;
import by.ralovets.majector.service.Injector;
import by.ralovets.majector.service.Provider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Objects.isNull;

public class InjectorImpl implements Injector {

    private final ClassBindingCreator bindingCreator = ClassBindingCreatorImpl.getInstance();

    List<Class<?>> types = new ArrayList<>();
    Map<Class<?>, ClassBinding> bindings = new HashMap<>();
    Map<Class<?>, Object> singletonsCache = new HashMap<>();

    /**
     * Returns class instance with all injections by interface class.
     */
    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (isNull(type)) return null;

        ClassBinding binding = findBinding(type);

        if (isNull(binding)) return null;

        if (binding.isSingleton()) {
            Object o = singletonsCache.get(type);
            if (isNull(o)) {
                o = instantiateObject(binding);
            }

            Object resultObject = o;
            return () -> (T) resultObject;
        } else {
            return () -> (T) instantiateObject(binding);
        }
    }

    private Object instantiateObject(ClassBinding binding) {
        Object[] args = Arrays.stream(binding.getConstructorArgsTypes())
                .map(c -> getProvider(c).getInstance())
                .toArray(Object[]::new);

        Constructor<?> constructor = binding.getInjectedConstructor();

        try {
            return constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    /**
     * Registers binding by interface class and its implementation.
     */
    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        ClassBinding binding = bindingCreator.getBinding(intf, impl);
        types.add(intf);
        bindings.put(intf, binding);
    }

    /**
     * Registers singleton class.
     */
    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        ClassBinding binding = bindingCreator.getSingletonBinding(intf, impl);
        types.add(intf);
        bindings.put(intf, binding);
    }

    // Private methods

    private ClassBinding findBinding(Class<?> type) {
        Class<?> c = findConcreteClass(type);

        if (isNull(c)) c = findSubclass(type);
        if (isNull(c)) return null;

        return bindings.get(c);
    }

    private Class<?> findConcreteClass(Class<?> c) {
        if (isNull(c)) return null;

        for (Class<?> t : types) {
            if (t.equals(c)) return t;
        }
        return null;
    }

    private Class<?> findSubclass(Class<?> c) {
        if (isNull(c)) return null;

        for (Class<?> t : types) {
            if (c.isAssignableFrom(t)) return t;
        }
        return null;
    }
}

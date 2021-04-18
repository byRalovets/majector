package by.ralovets.majector.service.impl;

import by.ralovets.majector.exception.BindingNotFoundException;
import by.ralovets.majector.exception.InstantiationException;
import by.ralovets.majector.exception.RecursiveInjectionException;
import by.ralovets.majector.model.ClassBinding;
import by.ralovets.majector.service.ClassBindingCreator;
import by.ralovets.majector.service.Injector;
import by.ralovets.majector.service.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Objects.isNull;

public class InjectorImpl implements Injector {

    private final ClassBindingCreator bindingCreator = ClassBindingCreatorImpl.getInstance();

    Map<Class<?>, ClassBinding> bindings = new HashMap<>();
    Map<Class<?>, Object> singletonsCache = new HashMap<>();
    Map<Long, Set<Class<?>>> injectionHistory = new HashMap<>();

    /**
     * Returns class instance with all injections by interface class.
     */
    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (isNull(type)) {
            throw new IllegalArgumentException();
        }

        ClassBinding binding = bindings.get(type);
        if (isNull(binding)) {
            return null;
        }

        Object resultObject;
        if (binding.isSingleton()) {
            resultObject = singletonsCache.computeIfAbsent(type, t -> instantiateObjectRecursively(type));
            return () -> (T) resultObject;
        } else {
            return () -> (T) instantiateObjectRecursively(type);
        }
    }

    /**
     * Registers binding by interface class and its implementation.
     */
    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        ClassBinding binding = bindingCreator.getBinding(intf, impl);
        bindings.put(intf, binding);
    }

    /**
     * Registers binding by interface class and its implementation.
     */
    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        ClassBinding binding = bindingCreator.getSingletonBinding(intf, impl);
        bindings.put(intf, binding);
    }

    private Object instantiateObjectRecursively(Class<?> type) {
        Long threadId = Thread.currentThread().getId();

        if (!injectionHistory.computeIfAbsent(threadId, t -> new HashSet<>()).add(type)) {
            throw new RecursiveInjectionException();
        }

        ClassBinding binding = bindings.get(type);

        if (isNull(binding)) {
            throw new BindingNotFoundException();
        }

        Object[] args = Arrays.stream(binding.getConstructorArgsTypes())
                .map(this::instantiateObjectRecursively).toArray();

        Object object;
        try {
            object = binding.getInjectedConstructor().newInstance(args);
        } catch (java.lang.InstantiationException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new InstantiationException(e.getMessage());
        }

        injectionHistory.get(threadId).remove(type);
        return object;
    }
}

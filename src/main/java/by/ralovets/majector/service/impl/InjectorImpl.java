package by.ralovets.majector.service.impl;

import by.ralovets.majector.exception.InstantiationException;
import by.ralovets.majector.exception.*;
import by.ralovets.majector.model.Binding;
import by.ralovets.majector.model.Scope;
import by.ralovets.majector.service.BindingCreator;
import by.ralovets.majector.service.Injector;
import by.ralovets.majector.service.Provider;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static java.util.Objects.isNull;

public class InjectorImpl implements Injector {

    private final BindingCreator bindingCreator = new BindingCreatorImpl();

    Map<Class<?>, Binding> bindings = new HashMap<>();
    Map<Class<?>, Object> singletonsCache = new HashMap<>();

    /*
     * This map is used to detect recursive injections.
     * Key is thread id; value is set of classes in current injection.
     * This is used as a stack, but the uniqueness of the elements is important, not the order
     */
    Map<Long, Set<Class<?>>> injectionHistory = new HashMap<>();

    /**
     * Returns a provider that is ready to provide you with objects of the requested type.
     *
     * @throws IllegalArgumentException    - if one of the arguments is null
     * @throws RecursiveInjectionException - if recursive injection occurs
     * @throws BindingNotFoundException    - if there is no binding to this type
     * @throws InstantiationException      - if an error occurred during instantiation
     */
    @Override
    public <T> Provider<T> getProvider(Class<T> type) {
        if (isNull(type)) {
            throw new IllegalArgumentException();
        }

        Binding binding = bindings.get(type);
        if (isNull(binding)) {
            return null;
        }

        // throws RecursiveInjectionException
        checkInfiniteRecursion(type);

        Object resultObject;
        if (binding.getScope().equals(Scope.SINGLETON)) {
            if (singletonsCache.containsKey(type)) {
                resultObject = singletonsCache.get(type);
            } else {
                resultObject = instantiateObjectRecursively(type);
                singletonsCache.put(type, resultObject);
            }
            return () -> (T) resultObject;
        } else {
            return () -> (T) instantiateObjectRecursively(type);
        }
    }

    /**
     * Registers binding.
     *
     * @throws IllegalArgumentException     - if one of the arguments is null
     * @throws TooManyConstructorsException - if there are more than two constructors with @Inject
     * @throws ConstructorNotFoundException - if there is no constructors (with @Inject or default)
     */
    @Override
    public <T> void bind(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        Binding binding = bindingCreator.getBinding(intf, impl);
        bindings.put(intf, binding);
    }

    /**
     * Registers singleton binding.
     *
     * @throws IllegalArgumentException     - if one of the arguments is null
     * @throws TooManyConstructorsException - if there are more than two constructors with @Inject
     * @throws ConstructorNotFoundException - if there is no constructors (with @Inject or default)
     */
    @Override
    public <T> void bindSingleton(Class<T> intf, Class<? extends T> impl) {
        if (isNull(intf) || isNull(impl)) {
            throw new IllegalArgumentException();
        }

        Binding binding = bindingCreator.getSingletonBinding(intf, impl);
        bindings.put(intf, binding);
    }

    /**
     * @throws BindingNotFoundException - if there is no binding to this type
     * @throws InstantiationException   - if an error occurred during instantiation
     */
    private Object instantiateObjectRecursively(Class<?> type) {
        Binding binding = bindings.get(type);

        if (isNull(binding)) {
            throw new BindingNotFoundException();
        }

        Object[] args = Arrays.stream(binding.getConstructor().getParameterTypes())
                .map(this::instantiateObjectRecursively).toArray();

        Object object;
        try {
            if (binding.getScope().equals(Scope.SINGLETON)) {
                object = singletonsCache.computeIfAbsent(type, t -> {
                    try {
                        return binding.getConstructor().newInstance(args);
                    } catch (java.lang.InstantiationException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
                        throw new InstantiationException(e.getMessage());
                    }
                });
            } else {
                object = binding.getConstructor().newInstance(args);
            }
        } catch (java.lang.InstantiationException | IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new InstantiationException(e.getMessage());
        }

        return object;
    }

    /**
     * @throws RecursiveInjectionException - if recursive injection occurs
     */
    private void checkInfiniteRecursion(Class<?> type) {
        Long threadId = Thread.currentThread().getId();

        if (!injectionHistory.computeIfAbsent(threadId, t -> new HashSet<>()).add(type)) {
            throw new RecursiveInjectionException();
        }

        Binding binding = bindings.get(type);

        if (isNull(binding)) {
            throw new BindingNotFoundException();
        }

        Arrays.stream(binding.getConstructor().getParameterTypes())
                .forEach(this::checkInfiniteRecursion);

        injectionHistory.get(threadId).remove(type);
    }
}

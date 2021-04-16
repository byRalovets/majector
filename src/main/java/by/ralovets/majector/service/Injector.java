package by.ralovets.majector.service;

public interface Injector {

    /**
     * Returns class instance with all injections by interface class.
     */
    <T> Provider<T> getProvider(Class<T> type);

    /**
     * Registers binding by interface class and its implementation.
     */
    <T> void bind(Class<T> intf, Class<? extends T> impl);

    /**
     * Registers singleton class.
     */
    <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);
}

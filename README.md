# Majector — magic injector for you!

Hi! This is the simplest version of the DI container.

## Features

1. No third-party libraries were used (except Junit 5).
2. The logic is implemented in the `InjectorImpl` class based on the `Injector` interface.
   ```java
   public interface Injector {  
   
       // Returns class instance with all injections by interface class.
       <T> Provider<T> getProvider(Class<T> type);  

       // Registers binding by interface class and its implementation.
       <T> void bind(Class<T> intf, Class<? extends T> impl);  

       // Registers singleton class.
       <T> void bindSingleton(Class<T> intf, Class<? extends T> impl);  
   }

   public interface Provider<T> {  
       T getInstance();  
   }
   ```
4. `@Inject` is added to the class constructor for binding.
5. If a class has multiple constructors with  `@Inject`, `TooManyConstructorsException` is thrown.
6. If there are no constructors with `@Inject`, default constructor is used. In the absence of such  `ConstructorNotFoundException` is thrown.
7. If the container uses a constructor with `@Inject` and the container
   cannot find binding for any argument, `BindingNotFoundException` is thrown.
8. If you are requesting `Provider` for a class and there is no corresponding binding, `null` is returned.
9. Ability to use `Singleton` and `Prototype` beans.
10. Lazy `Singleton` beans.
11. Injection only via constructors.
12. All constructor arguments are guaranteed to be reference types.
13. All constructors are `public`.

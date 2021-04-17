package by.ralovets.majector.model;

import java.lang.reflect.Constructor;

public class ClassBinding {

    private Class<?> intfType;
    private Class<?> implType;
    private Constructor<?> injectedConstructor;
    private Class<?>[] constructorArgsTypes = new Class<?>[0];
    private boolean isSingleton;

    public ClassBinding() {
    }

    public ClassBinding(Class<?> intfType, Class<?> implType, Constructor<?> injectedConstructor, Class<?>[] constructorArgsTypes, boolean isSingleton) {
        this.intfType = intfType;
        this.implType = implType;
        this.injectedConstructor = injectedConstructor;
        this.constructorArgsTypes = constructorArgsTypes;
        this.isSingleton = isSingleton;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    public void setSingleton(boolean singleton) {
        isSingleton = singleton;
    }

    public Class<?> getIntfType() {
        return intfType;
    }

    public void setIntfType(Class<?> intfType) {
        this.intfType = intfType;
    }

    public Class<?> getImplType() {
        return implType;
    }

    public void setImplType(Class<?> implType) {
        this.implType = implType;
    }

    public Class<?>[] getConstructorArgsTypes() {
        return constructorArgsTypes;
    }

    public void setConstructorArgsTypes(Class<?>[] constructorArgsTypes) {
        this.constructorArgsTypes = constructorArgsTypes;
    }

    public Constructor<?> getInjectedConstructor() {
        return injectedConstructor;
    }

    public void setInjectedConstructor(Constructor<?> injectedConstructor) {
        this.injectedConstructor = injectedConstructor;
    }
}


package by.ralovets.majector.model;

import java.lang.reflect.Constructor;

public class Binding {

    private Class<?> intfType;
    private Class<?> implType;
    private Constructor<?> constructor;
    private Scope scope;

    public Binding() {
    }

    public Binding(Class<?> intfType, Class<?> implType, Constructor<?> constructor, Scope scope) {
        this.intfType = intfType;
        this.implType = implType;
        this.constructor = constructor;
        this.scope = scope;
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

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
}


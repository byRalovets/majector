package by.ralovets.majector.service;

import by.ralovets.majector.model.Binding;

public interface BindingCreator {

    <T> Binding getBinding(Class<T> intf, Class<? extends T> impl);
    <T> Binding getSingletonBinding(Class<T> intf, Class<? extends T> impl);
}

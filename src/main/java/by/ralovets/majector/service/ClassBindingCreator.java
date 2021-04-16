package by.ralovets.majector.service;

import by.ralovets.majector.model.ClassBinding;

public interface ClassBindingCreator {

    <T> ClassBinding getBinding(Class<T> intf, Class<? extends T> impl);
    <T> ClassBinding getSingletonBinding(Class<T> intf, Class<? extends T> impl);
}

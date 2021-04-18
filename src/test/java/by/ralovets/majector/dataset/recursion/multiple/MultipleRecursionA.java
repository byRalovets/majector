package by.ralovets.majector.dataset.recursion.multiple;

import by.ralovets.majector.annotation.Inject;

public class MultipleRecursionA {

    private MultipleRecursionB b;

    @Inject
    public MultipleRecursionA(MultipleRecursionB b) {
        this.b = b;
    }
}

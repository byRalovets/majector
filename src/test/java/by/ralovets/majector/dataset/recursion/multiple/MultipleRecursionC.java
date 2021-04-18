package by.ralovets.majector.dataset.recursion.multiple;

import by.ralovets.majector.annotation.Inject;

public class MultipleRecursionC {

    private MultipleRecursionA a;

    @Inject
    public MultipleRecursionC(MultipleRecursionA a) {
        this.a = a;
    }
}

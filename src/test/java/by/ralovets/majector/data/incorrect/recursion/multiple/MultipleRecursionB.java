package by.ralovets.majector.data.incorrect.recursion.multiple;

import by.ralovets.majector.annotation.Inject;

public class MultipleRecursionB {

    private MultipleRecursionC c;

    @Inject
    public MultipleRecursionB(MultipleRecursionC c) {
        this.c = c;
    }
}

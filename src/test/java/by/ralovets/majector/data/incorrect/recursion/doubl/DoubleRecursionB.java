package by.ralovets.majector.data.incorrect.recursion.doubl;

import by.ralovets.majector.annotation.Inject;

public class DoubleRecursionB {

    private DoubleRecursionA a;

    @Inject
    public DoubleRecursionB(DoubleRecursionA a) {
        this.a = a;
    }
}

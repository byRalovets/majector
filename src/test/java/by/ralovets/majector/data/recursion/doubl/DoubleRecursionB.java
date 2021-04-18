package by.ralovets.majector.data.recursion.doubl;

import by.ralovets.majector.annotation.Inject;

public class DoubleRecursionB {

    private DoubleRecursionA a;

    @Inject
    public DoubleRecursionB(DoubleRecursionA a) {
        this.a = a;
    }
}

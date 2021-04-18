package by.ralovets.majector.data.recursion.doubl;

import by.ralovets.majector.annotation.Inject;

public class DoubleRecursionA {

    private DoubleRecursionB b;

    @Inject
    public DoubleRecursionA(DoubleRecursionB b) {
        this.b = b;
    }
}

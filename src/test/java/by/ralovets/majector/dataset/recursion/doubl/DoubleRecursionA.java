package by.ralovets.majector.dataset.recursion.doubl;

import by.ralovets.majector.annotation.Inject;

public class DoubleRecursionA {

    private DoubleRecursionB b;

    @Inject
    public DoubleRecursionA(DoubleRecursionB b) {
        this.b = b;
    }
}

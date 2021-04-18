package by.ralovets.majector.dataset.recursion.doubl;

import by.ralovets.majector.annotation.Inject;

public class DoubleRecursionB {

    private DoubleRecursionA a;

    @Inject
    public DoubleRecursionB(DoubleRecursionA a) {
        this.a = a;
    }
}

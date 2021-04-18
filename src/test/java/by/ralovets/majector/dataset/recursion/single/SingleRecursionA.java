package by.ralovets.majector.dataset.recursion.single;

import by.ralovets.majector.annotation.Inject;

public class SingleRecursionA {

    private SingleRecursionA a;

    @Inject
    public SingleRecursionA(SingleRecursionA singleRecursionA) {
        this.a = singleRecursionA;
    }
}

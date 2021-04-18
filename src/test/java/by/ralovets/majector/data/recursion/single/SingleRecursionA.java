package by.ralovets.majector.data.recursion.single;

import by.ralovets.majector.annotation.Inject;

public class SingleRecursionA {

    private SingleRecursionA a;

    @Inject
    public SingleRecursionA(SingleRecursionA singleRecursionA) {
        this.a = singleRecursionA;
    }
}

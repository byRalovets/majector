package by.ralovets.majector.dataset.constructor.toomany;

import by.ralovets.majector.annotation.Inject;

public class TooManyConstructorsClass02 implements ToManyConstructorsInterface {

    @Inject
    public TooManyConstructorsClass02(Integer i) {
    }

    @Inject
    public TooManyConstructorsClass02(Object o) {
    }
}

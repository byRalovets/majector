package by.ralovets.majector.dataset.constructor.toomany;

import by.ralovets.majector.annotation.Inject;

// This class has too many constructors with @Inject
public class TooManyConstructorsClass01 implements ToManyConstructorsInterface {

    @Inject
    public TooManyConstructorsClass01() {
    }

    @Inject
    public TooManyConstructorsClass01(Object o) {

    }
}

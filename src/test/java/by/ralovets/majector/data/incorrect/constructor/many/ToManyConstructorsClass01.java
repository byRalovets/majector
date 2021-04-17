package by.ralovets.majector.data.incorrect.constructor.many;

import by.ralovets.majector.annotation.Inject;
import by.ralovets.majector.data.correct.EventDao;

// This class has too many constructors with @Inject
public class ToManyConstructorsClass01 implements ToManyConstructorsInterface {

    @Inject
    public ToManyConstructorsClass01() {
    }

    @Inject
    public ToManyConstructorsClass01(Object o) {

    }
}

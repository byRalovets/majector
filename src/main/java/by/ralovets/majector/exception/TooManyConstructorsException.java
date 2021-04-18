package by.ralovets.majector.exception;

public class TooManyConstructorsException extends RuntimeException {

    public TooManyConstructorsException() {
    }

    public TooManyConstructorsException(String message) {
        super(message);
    }
}

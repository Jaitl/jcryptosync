package com.jcryptosync.exceptoins;

public class NoCorrectCompositeKeyException extends Exception {
    public NoCorrectCompositeKeyException() {
    }

    public NoCorrectCompositeKeyException(String message) {
        super(message);
    }
}

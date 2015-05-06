package com.jcryptosync.container.exceptoins;

public class NoCorrectPasswordException extends Exception {
    public NoCorrectPasswordException(String message) {
        super(message);
    }
}

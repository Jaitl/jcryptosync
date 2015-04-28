package com.jcryptosync.exceptoins;

public class NoCorrectPasswordException extends Exception {
    public NoCorrectPasswordException(String message) {
        super(message);
    }
}

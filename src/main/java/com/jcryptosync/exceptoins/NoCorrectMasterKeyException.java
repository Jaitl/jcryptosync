package com.jcryptosync.exceptoins;

public class NoCorrectMasterKeyException extends Exception {
    public NoCorrectMasterKeyException() {
    }

    public NoCorrectMasterKeyException(String message) {
        super(message);
    }
}

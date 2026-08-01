package de.nihas101.midas.lock.service;

public class LockedException extends IllegalStateException {

    public LockedException(final String message) {
        super(message);
    }
}

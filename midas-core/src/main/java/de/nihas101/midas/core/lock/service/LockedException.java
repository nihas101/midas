package de.nihas101.midas.core.lock.service;

public class LockedException extends IllegalStateException {

    public LockedException(final String message) {
        super(message);
    }
}

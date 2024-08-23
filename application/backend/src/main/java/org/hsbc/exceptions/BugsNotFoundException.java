package org.hsbc.exceptions;

public class BugsNotFoundException extends Exception {
    public BugsNotFoundException() {
    }

    public BugsNotFoundException(String message) {
        super(message);
    }

    public BugsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BugsNotFoundException(Throwable cause) {
        super(cause);
    }
}

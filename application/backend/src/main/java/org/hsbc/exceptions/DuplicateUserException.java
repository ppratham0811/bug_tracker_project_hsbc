package org.hsbc.exceptions;

public class DuplicateUserException extends Exception {
    public DuplicateUserException() {
        super();
    }

    public DuplicateUserException(String msg) {
        super(msg);
    }

    public DuplicateUserException(String msg, Throwable t) {
        super(msg, t);
    }

    public DuplicateUserException(Throwable t) {
        super(t);
    }
}

package org.hsbc.exceptions;

public class NoSuchBugException extends Exception{
    public NoSuchBugException() {
    }

    public NoSuchBugException(String message) {
        super(message);
    }

    public NoSuchBugException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSuchBugException(Throwable cause) {
        super(cause);
    }
}

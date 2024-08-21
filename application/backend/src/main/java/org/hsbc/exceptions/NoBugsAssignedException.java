package org.hsbc.exceptions;

public class NoBugsAssignedException extends Exception{
    public NoBugsAssignedException() {
    }

    public NoBugsAssignedException(String message) {
        super(message);
    }

    public NoBugsAssignedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoBugsAssignedException(Throwable cause) {
        super(cause);
    }
}

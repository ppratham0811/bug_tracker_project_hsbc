package org.hsbc.exceptions;

public class BugNotAcceptedException extends Exception {
    public BugNotAcceptedException() {
        super();
    }

    public BugNotAcceptedException(String msg) {
        super(msg);
    }

    public BugNotAcceptedException(Throwable t) {
        super(t);
    }

    public BugNotAcceptedException(String msg, Throwable t) {
        super(msg, t);
    }
}

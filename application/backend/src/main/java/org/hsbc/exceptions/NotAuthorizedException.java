package org.hsbc.exceptions;

public class NotAuthorizedException extends Exception {
    public NotAuthorizedException() {
        super();
    }

    public NotAuthorizedException(String msg) {
        super(msg);
    }

    public NotAuthorizedException(String msg, Throwable t) {
        super(msg, t);
    }

    public NotAuthorizedException(Throwable t) {
        super(t);
    }

}

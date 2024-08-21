package org.hsbc.exceptions;

public class ProjectNotFoundException extends Exception {
    public ProjectNotFoundException() {
        super();
    }

    public ProjectNotFoundException(String msg) {
        super(msg);
    }

    public ProjectNotFoundException(Throwable t) {
        super(t);
    }

    public ProjectNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }
}

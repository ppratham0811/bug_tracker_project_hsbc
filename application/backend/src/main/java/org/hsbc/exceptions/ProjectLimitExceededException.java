package org.hsbc.exceptions;

public class ProjectLimitExceededException extends Exception {
    public ProjectLimitExceededException() {
        super();
    }

    public ProjectLimitExceededException(String msg) {
        super(msg);
    }

    public ProjectLimitExceededException(String msg, Throwable t) {
        super(msg, t);
    }

    public ProjectLimitExceededException(Throwable t) {
        super(t);
    }
}

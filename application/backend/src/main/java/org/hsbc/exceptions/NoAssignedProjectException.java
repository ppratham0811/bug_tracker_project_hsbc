package org.hsbc.exceptions;

public class NoAssignedProjectException extends Exception{
    public NoAssignedProjectException() {
    }

    public NoAssignedProjectException(String message) {
        super(message);
    }

    public NoAssignedProjectException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAssignedProjectException(Throwable cause) {
        super(cause);
    }
}

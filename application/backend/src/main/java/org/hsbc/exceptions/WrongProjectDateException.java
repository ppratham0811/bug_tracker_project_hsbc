package org.hsbc.exceptions;

public class WrongProjectDateException extends Exception {
    public WrongProjectDateException() {
        super();
    }

    public WrongProjectDateException(String msg) {
        super(msg);
    }

    public WrongProjectDateException(String msg, Throwable t) {
        super(msg, t);
    }

    public WrongProjectDateException(Throwable t) {
        super(t);
    }
}

package ru.airiva.exception;

/**
 * @author Ivan
 */
public class BsException extends Exception {

    public BsException() {
    }

    public BsException(String message) {
        super(message);
    }

    public BsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BsException(Throwable cause) {
        super(cause);
    }

}

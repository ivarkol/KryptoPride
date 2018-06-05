package ru.airiva.exception;

/**
 * @author Ivan
 */
public class TlgFailAuthBsException extends BsException {
    public TlgFailAuthBsException() {
        super("Во время авторизации произошла ошибка");
    }

    public TlgFailAuthBsException(String message) {
        super(message);
    }
}

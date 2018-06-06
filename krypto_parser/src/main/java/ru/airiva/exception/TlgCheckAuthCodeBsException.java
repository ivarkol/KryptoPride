package ru.airiva.exception;

/**
 * @author Ivan
 */
public class TlgCheckAuthCodeBsException extends BsException {
    public TlgCheckAuthCodeBsException() {
        super("Во время проверки кода произошла ошибка");
    }
}

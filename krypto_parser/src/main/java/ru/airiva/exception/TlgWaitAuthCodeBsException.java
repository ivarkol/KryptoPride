package ru.airiva.exception;

/**
 * @author Ivan
 */
public class TlgWaitAuthCodeBsException extends BsException{
    public TlgWaitAuthCodeBsException() {
        super("Пришлите код аутентификации");
    }
}

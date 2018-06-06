package ru.airiva.exception;

/**
 * @author Ivan
 */
public class TlgNeedAuthBsException extends BsException {
    public TlgNeedAuthBsException() {
        super("Необходима авторизация клиента");
    }
}

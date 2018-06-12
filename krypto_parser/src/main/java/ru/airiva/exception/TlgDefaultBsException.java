package ru.airiva.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ivan
 */
public class TlgDefaultBsException extends BsException{
    private static final Logger LOGGER = LoggerFactory.getLogger(TlgDefaultBsException.class);
    public TlgDefaultBsException(Exception e) {
        super("Произошла внутренняя ошибка");
        LOGGER.error("Default error", e);
    }
}

package ru.airiva.client;

import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;

import java.util.List;

public interface TlgInteraction {

    /**
     * Авторизация клиента
     *
     * @param phone телефон клиента
     */
    void authorize(String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException;

    /**
     * Запуск парсинга для текущего клиента
     *
     * @param phone телефон клиента
     */
    void start(String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException;

    void checkCode(String code);

    void logout();

    List<String> getChats();

}

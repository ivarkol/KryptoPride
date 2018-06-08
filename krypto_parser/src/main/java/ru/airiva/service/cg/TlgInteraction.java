package ru.airiva.service.cg;

import ru.airiva.exception.TlgCheckAuthCodeBsException;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.vo.TlgChannel;

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

    /**
     * Проверка кода аутентификации клиента
     *
     * @param code  код
     * @param phone телефон клиента
     * @return результат проверки
     */
    boolean checkCode(String phone, String code) throws TlgNeedAuthBsException, TlgCheckAuthCodeBsException;

    /**
     * Логоут клиента
     *
     * @param phone телефон клиента
     */
    void logout(String phone);

    /**
     * Отсортированный по названию список аналов клиента
     *
     * @param phone телефон клиента
     */
    List<TlgChannel> getSortedChannels(String phone);

}

package ru.airiva.service.cg;

import ru.airiva.exception.*;
import ru.airiva.vo.TlgChannel;

import java.util.List;

public interface TlgInteraction {

    /**
     * Авторизация клиента
     *
     * @param phone телефон клиента
     */
    void authorize(String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Запуск парсинга для текущего клиента
     *
     * @param phone телефон клиента
     */
    void startParsing(String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Остановка парсинга для текущего клиента
     *
     * @param phone телефон клиента
     */
    void stopParsing(String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Статус активности парсинга текущего клиента
     *
     * @param phone телефон клиента
     * @return true - парсинг активен, иначе - false
     */
    boolean isEnableParsing(String phone);

    /**
     * Проверка кода аутентификации клиента
     *
     * @param code  код
     * @param phone телефон клиента
     * @return результат проверки
     */
    boolean checkCode(String phone, String code) throws TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

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
    List<TlgChannel> getSortedChannels(String phone)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Добавление парсинга из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     */
    void includeParsing(String phone, long source, long target)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Исключение парсинга из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     */
    void excludeParsing(String phone, long source, long target)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Изменение задержки отправки сообщения из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     * @param delay  задержка отправки сообщения
     */
    void setMessageSendingDelay(String phone, long source, long target, long delay)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Добавление шаблона для парсинга из канала-источника в канал-потребитель для текущего клиента
     *
     * @param phone       телефон текущего клиента
     * @param source      идентификатор канала-источника
     * @param target      идентификатор канала-потребителя
     * @param search      шаблон для поиска
     * @param replacement шаблон для замены
     * @param order       позиция в списке шаблонов курьера
     */
    void addParsingExpression(String phone, long source, long target, String search, String replacement, int order)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Удаление шаблона для парсинга из канала-источника в канал-потребитель для текущего клиента
     *
     * @param phone       телефон текущего клиента
     * @param source      идентификатор канала-источника
     * @param target      идентификатор канала-потребителя
     * @param search      шаблон для поиска
     * @param replacement шаблон для замены
     */
    void removeParsingExpression(String phone, long source, long target, String search, String replacement)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException;

    /**
     * Повторная отправка кода аутентификации
     *
     * @param phone телефон клиента
     * @return тип кода (смс, чат)
     */
    String resendCode(String phone) throws TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException;
}

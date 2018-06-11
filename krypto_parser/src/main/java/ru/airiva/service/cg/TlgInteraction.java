package ru.airiva.service.cg;

import ru.airiva.exception.TlgDefaultBsException;
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
    void authorize(String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException, TlgDefaultBsException;

    /**
     * Запуск парсинга для текущего клиента
     *
     * @param phone телефон клиента
     */
    void startParsing(String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException;

    /**
     * Остановка парсинга для текущего клиента
     *
     * @param phone телефон клиента
     */
    void stopParsing(String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException;

    /**
     * Проверка кода аутентификации клиента
     *
     * @param code  код
     * @param phone телефон клиента
     * @return результат проверки
     */
    boolean checkCode(String phone, String code) throws TlgNeedAuthBsException, TlgDefaultBsException;

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
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException;

    /**
     * Добавление парсинга из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     * @param delay  задержка перед отправкой сообщения в секундах
     */
    void includeParsing(String phone, long source, long target, long delay)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException;

    /**
     * Исключение парсинга из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     */
    void excludeParsing(String phone, long source, long target)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException;

    /**
     * Изменение задержки отправки сообщения из канала {@code source} в канал {@code target}
     *
     * @param phone  телефон клиента
     * @param source идентификатор канала источника
     * @param target идентификатор канала потребителя
     * @param delay  задержка отправки сообщения
     */
    void setMessageSendingDelay(String phone, long source, long target, long delay)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException;

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
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException;

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
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException;

}

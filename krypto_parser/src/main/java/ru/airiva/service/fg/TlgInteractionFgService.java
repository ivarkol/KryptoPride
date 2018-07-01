package ru.airiva.service.fg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.airiva.client.TlgClient;
import ru.airiva.entities.TlgChatPairEntity;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgTimeoutBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.parser.Courier;
import ru.airiva.parser.Expression;
import ru.airiva.parser.Parser;
import ru.airiva.properties.Timeouts;
import ru.airiva.service.da.TlgInteractionDaService;
import ru.airiva.tdlib.TdApi;
import ru.airiva.vo.TlgChannel;
import ru.airiva.vo.TlgChat;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Ivan
 */
@Service
public class TlgInteractionFgService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlgInteractionFgService.class);

    /**
     * Блокировки по номеру телефона
     */
    private static final Map<String, Lock> LOCKS_BY_PHONE = new HashMap<>();
    private static final Map<String, TlgClient> CLIENTS = new HashMap<>();

    private TlgInteractionDaService tlgInteractionDaService;
    private KryptoParserInitializeFgService kryptoParserInitializeFgService;
    private Timeouts timeouts;

    @Autowired
    public void setTlgInteractionDaService(TlgInteractionDaService tlgInteractionDaService) {
        this.tlgInteractionDaService = tlgInteractionDaService;
    }

    @Autowired
    public void setKryptoParserInitializeFgService(KryptoParserInitializeFgService kryptoParserInitializeFgService) {
        this.kryptoParserInitializeFgService = kryptoParserInitializeFgService;
    }

    @Autowired
    public void setTimeouts(Timeouts timeouts) {
        this.timeouts = timeouts;
    }

    /**
     * Проверка авторизации клиента
     *
     * @param phone номер телефона клиента
     * @return клиент, если он уже авторизован
     * @throws TlgNeedAuthBsException     если клиент не авторизован
     * @throws InterruptedException       если поток был прерван
     * @throws TlgWaitAuthCodeBsException если авторизация клиента на шаге подтверждения кода аутентификации
     */
    private TlgClient checkAuth(final String phone) throws TlgNeedAuthBsException, InterruptedException, TlgWaitAuthCodeBsException, TlgTimeoutBsException {
        TlgClient tlgClient = CLIENTS.get(phone);
        if (tlgClient == null) throw new TlgNeedAuthBsException();
        //Запрашиваем статус авторизации клиента
        int authorizationState = tlgInteractionDaService.getAuthorizationState(tlgClient).getConstructor();
        switch (authorizationState) {
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                //Если клиент ждет код
                throw new TlgWaitAuthCodeBsException();
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                //Если клиент авторизован
                return tlgClient;
            default:
                //Если другие статусы, то авторизации нет
                CLIENTS.remove(phone);
                throw new TlgNeedAuthBsException();
        }
    }

    /**
     * Авторизация клиента
     *
     * @param phone номер телефона клиента в формате +7ХХХХХХХХХ
     * @throws TlgWaitAuthCodeBsException если авторизация клиента на шаге подтверждения кода аутентификации
     * @throws InterruptedException       если поток был прерван
     * @throws TlgFailAuthBsException     если во время авторизации сервер вернул ошибку
     * @throws TlgTimeoutBsException      если время ожидания истекло
     */
    public void auth(final String phone) throws TlgWaitAuthCodeBsException, InterruptedException, TlgFailAuthBsException, TlgTimeoutBsException {


        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            try {
                //Проверяем текущий статус авторизации у клиента
                checkAuth(phone);
            } catch (TlgNeedAuthBsException e) {
                //Авторизовываем заново
                TlgClient tlgClient = new TlgClient(phone);
                TdApi.Object authStep;
                try {
                    authStep = tlgClient.updatesHandler.authExchanger.exchange(null, timeouts.auth, SECONDS);
                } catch (TimeoutException te) {
                    Thread.currentThread().interrupt();
                    throw new TlgTimeoutBsException("Истекло время ожидания авторизации", te);
                }
                switch (authStep.getConstructor()) {
                    case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                        CLIENTS.put(phone, tlgClient);
                        throw new TlgWaitAuthCodeBsException();
                    case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                        CLIENTS.put(phone, tlgClient);
                        initializeCouriers(tlgClient);
                        break;
                    case TdApi.Error.CONSTRUCTOR:
                        tlgInteractionDaService.closeClient(tlgClient);
                        throw new TlgFailAuthBsException(((TdApi.Error) authStep).message);
                }
            }
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Проверка кода авторизации текущего клиента
     *
     * @param phone номер телефона клиента
     * @param code  код авторизации
     * @return результат проверки
     */
    public boolean checkCode(final String phone, final String code) throws TlgNeedAuthBsException, InterruptedException, TlgTimeoutBsException {
        boolean result;
        Lock lockByPhone = getLockByPhone(phone);

        lockByPhone.lock();
        try {
            checkAuth(phone);
            //Если клиент уже авторизовался
            result = true;
        } catch (TlgWaitAuthCodeBsException e) {
            //Если клиент ждёт код аутентификации
            TlgClient tlgClient = CLIENTS.get(phone);
            try {
                tlgClient.updatesHandler.setFromWaitCodeState(true);
                tlgClient.client.send(
                        new TdApi.CheckAuthenticationCode(code, "", ""),
                        tlgClient.checkAuthenticationCodeHandler);

                TdApi.Object state;
                try {
                    state = tlgClient.updatesHandler.checkCodeExchanger.exchange(null, timeouts.codeCheck, SECONDS);
                } catch (TimeoutException e1) {
                    throw new TlgTimeoutBsException("Истекло время ожидания проверки кода аутентификации", e);
                }
                switch (state.getConstructor()) {
                    case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                        result = true;
                        break;
                    case TdApi.Error.CONSTRUCTOR:
                        result = false;
                        break;
                    default:
                        result = false;
                        break;
                }
            } finally {
                tlgClient.updatesHandler.setFromWaitCodeState(false);
            }

        } finally {
            lockByPhone.unlock();
        }
        return result;
    }

    /**
     * Повторная отправка кода аутентификации
     *
     * @param phone телефон клиента
     * @return тип кода (смс, чат)
     */
    public String resendCode(String phone) throws InterruptedException, TlgNeedAuthBsException, TlgTimeoutBsException {
        String codeType = "unknown";
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            checkAuth(phone);
        } catch (TlgWaitAuthCodeBsException e) {
            //Код отправляем только, если клиент в статусе ожидания кода
            TlgClient tlgClient = CLIENTS.get(phone);
            tlgClient.client.send(new TdApi.ResendAuthenticationCode(), object -> {
                switch (object.getConstructor()) {
                    case TdApi.Ok.CONSTRUCTOR:
                        LOGGER.debug("Authentication code resend successful");
                        break;
                    case TdApi.Error.CONSTRUCTOR:
                        LOGGER.warn("Authentication code resend unsuccessful");
                        break;
                    default:
                        LOGGER.warn("When authentication code resend receive wrong response from TDLib: {}", object);
                        break;
                }
            });
            TdApi.Object exchange;
            try {
                exchange = tlgClient.updatesHandler.authExchanger.exchange(null, timeouts.codeResend, SECONDS);
            } catch (TimeoutException te) {
                throw new TlgTimeoutBsException("Истекло время ожидания при отправке кода аутентификации", te);
            }
            if (exchange.getConstructor() == TdApi.AuthorizationStateWaitCode.CONSTRUCTOR) {
                TdApi.AuthorizationStateWaitCode authorizationStateWaitCode = ((TdApi.AuthorizationStateWaitCode) exchange);
                switch (authorizationStateWaitCode.codeInfo.type.getConstructor()) {
                    case TdApi.AuthenticationCodeTypeSms.CONSTRUCTOR:
                        codeType = "SMS";
                        break;
                    case TdApi.AuthenticationCodeTypeTelegramMessage.CONSTRUCTOR:
                        codeType = "Telegram message";
                        break;
                }
            }
        } finally {
            lockByPhone.unlock();
        }
        return codeType;
    }

    /**
     * Логоут клиента
     *
     * @param phone телефон клиента
     */
    public void logout(final String phone) {
        Lock lockByPhone = getLockByPhone(phone);

        lockByPhone.lock();
        try {
            TlgClient tlgClient = CLIENTS.get(phone);
            if (tlgClient != null) {
                tlgClient.client.send(new TdApi.LogOut(), object -> {
                    switch (object.getConstructor()) {
                        case TdApi.Error.CONSTRUCTOR:
                            LOGGER.error("Client {} unsuccessful logout: {} {}",
                                    tlgClient.phone,
                                    ((TdApi.Error) object).code,
                                    ((TdApi.Error) object).message);
                            break;
                    }
                });
                try {
                    tlgClient.updatesHandler.logoutLatch.await(timeouts.logout, SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.error("Logout was interrupted", e);
                }
                CLIENTS.remove(phone);
            }
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Каналы клиента
     *
     * @param phone телефон клиента
     * @return набор идентификаторов чатов
     */
    public Collection<TlgChannel> getChannels(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);

        lockByPhone.lock();
        Collection<TlgChannel> channels;
        try {
            TlgClient tlgClient = checkAuth(phone);
            if (!tlgClient.updatesHandler.chatsInitialized) {
                initializeChats(tlgClient);
            }
            channels = tlgClient.channels.values();
            //Перекладываем названия и id чатов в каналы
            channels.forEach(tlgChannel -> {
                String title = tlgClient.updatesHandler.supergroupId2Title.get(tlgChannel.id);
                tlgChannel.setTitle(title != null && !title.isEmpty() ? title : "unknown");
                tlgChannel.setChatId(tlgClient.updatesHandler.supergroupId2ChatId.get(tlgChannel.id));
            });
        } finally {
            lockByPhone.unlock();
        }

        return channels;
    }

    /**
     * Активация парсинга клиента
     *
     * @param phone телефон клиента
     */
    public void enableParsing(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            tlgClient.updatesHandler.dispatcher.enable();
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Остановка парсинга клиента
     *
     * @param phone телефон клиента
     */
    public void disableParsing(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            tlgClient.updatesHandler.dispatcher.disable();
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Добавление курьера в диспетчер
     *
     * @param phone   номер телефона клиента
     * @param courier курьер
     */
    public void addCourier(final String phone, final Courier courier) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        if (courier == null) return;
        Assert.notNull(courier.parser, "У курьера должен быть шаблон");
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            tlgClient.updatesHandler.dispatcher.putCourier(courier);
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Добавление нескольких курьеров в диспетчер
     *
     * @param phone    номер телефона клиента
     * @param couriers набор курьеров
     */
    public void addCouriers(final String phone, final Collection<Courier> couriers) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            couriers.forEach(tlgClient.updatesHandler.dispatcher::putCourier);
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Удаление курьера из диспетчера
     *
     * @param phone    телефон клиента
     * @param template шаблон, по которому нужно удалить курьера
     */
    public void deleteCourier(final String phone, final Courier template) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            tlgClient.updatesHandler.dispatcher.deleteCourier(template);
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Удаление набора курьеров из диспетчера
     *
     * @param phone    телефон клиента
     * @param couriers коллекция шаблонов курьеров для удаления
     */
    public void deleteCouriers(final String phone, final Collection<Courier> couriers) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            couriers.forEach(tlgClient.updatesHandler.dispatcher::deleteCourier);
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Изменение времени задержки отправки сообщения курьером текущего клиента
     *
     * @param phone номер телефона клиента
     * @param delay задержка отправки сообщения
     */
    public void setCourierDelay(final String phone, final Courier template, final long delay) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            Courier courier = tlgClient.updatesHandler.dispatcher.findCourier(template);
            if (courier != null) {
                courier.setDelay(delay);
            }
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Добавление шаблона для парсинга курьеру клиента
     *
     * @param phone      номер телефона клиента
     * @param template   шаблон курьера для поиска курьера
     * @param expression шаблон для добавления в курьер
     */
    public void addExpression(final String phone, final Courier template, final Expression expression) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            Courier courier = tlgClient.updatesHandler.dispatcher.findCourier(template);
            if (courier != null && courier.parser != null) {
                courier.parser.addExpression(expression);
            }
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Удаление шаблона для парсинга из курьера клиента
     *
     * @param phone      нормер телефона клиента
     * @param template   шаблон курьера для поиска курьера
     * @param expression шаблон для удаления из курьера
     */
    public void removeExpression(final String phone, final Courier template, final Expression expression) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgTimeoutBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            Courier courier = tlgClient.updatesHandler.dispatcher.findCourier(template);
            if (courier != null && courier.parser != null) {
                courier.parser.removeExpression(expression);
            }
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Добавление курьеров в диспетчер из БД
     *
     * @param tlgClient текущий клиент
     */
    private void initializeCouriers(TlgClient tlgClient) {
        if (tlgClient == null) return;
        List<Courier> couriers = new ArrayList<>();
        Set<TlgChatPairEntity> pairs = kryptoParserInitializeFgService.obtainEnabledTlgChatPairs(tlgClient.phone);
        pairs.forEach(pair -> couriers.add(
                new Courier(
                        pair.getSrcChat().getTlgChatId(),
                        pair.getDestChat().getTlgChatId(),
                        new Parser(pair.getOrderedExpressionEntities()),
                        pair.getDelay())));
        try {
            addCouriers(tlgClient.phone, couriers);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Couriers initialization was interrupted", e);
        } catch (Exception e) {
            LOGGER.warn("Couriers initialization exception", e);
        }
    }

    /**
     * Загружает чаты клиента
     *
     * @param tlgClient клиент
     */
    private void initializeChats(final TlgClient tlgClient) {

        final CyclicBarrier chatsBarrier = new CyclicBarrier(2);

        long offsetOrder = Long.MAX_VALUE;
        long offsetChatId = 0;
        int limit = 20;

        while (!tlgClient.updatesHandler.chatsInitialized) {
            synchronized (tlgClient.updatesHandler.orderedChats) {
                if (!tlgClient.updatesHandler.chatsInitialized) {
                    if (!tlgClient.updatesHandler.orderedChats.isEmpty()) {
                        TlgChat first = tlgClient.updatesHandler.orderedChats.first();
                        offsetOrder = first.getOrder();
                        offsetChatId = first.chatId;
                    }
                    tlgClient.client.send(new TdApi.GetChats(offsetOrder, offsetChatId, limit), object -> {
                        switch (object.getConstructor()) {
                            case TdApi.Error.CONSTRUCTOR:
                                LOGGER.error("Receive an error for GetChats: {}", object);
                                break;
                            case TdApi.Chats.CONSTRUCTOR:
                                long[] chatIds = ((TdApi.Chats) object).chatIds;
                                if (chatIds.length == 0) {
                                    tlgClient.updatesHandler.chatsInitialized = true;
                                }
                                try {
                                    chatsBarrier.await(timeouts.getChats, SECONDS);
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    chatsBarrier.reset();
                                    LOGGER.error("Initialize chats was interrupted", e);
                                } catch (BrokenBarrierException e) {
                                    LOGGER.error("Chats initialize barrier was broken", e);
                                } catch (TimeoutException e) {
                                    LOGGER.warn("Chats waiting timeout elapsed", e);
                                }
                                break;
                            default:
                                LOGGER.error("Receive wrong response from TDLib: {}", object);
                        }
                    });
                }
            }
            try {
                chatsBarrier.await(timeouts.getChats, SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("Chats initialize was interrupted", e);
                chatsBarrier.reset();
            } catch (BrokenBarrierException e) {
                LOGGER.error("Chats initialize barrier was broken", e);
            } catch (TimeoutException e) {
                LOGGER.warn("Chats waiting timeout elapsed", e);
            }
        }
        tlgClient.updatesHandler.chats.clear();
        tlgClient.updatesHandler.orderedChats.clear();
        LOGGER.debug("Chats have initialized");

    }

    /**
     * Извлечение блокировки по номеру телефона
     * Если блокировки еще нет, то создает новую
     *
     * @param phone телефон клиента
     * @return блокировка
     */
    private Lock getLockByPhone(String phone) {
        Lock lock;
        synchronized (LOCKS_BY_PHONE) {
            if (LOCKS_BY_PHONE.containsKey(phone)) {
                lock = LOCKS_BY_PHONE.get(phone);
            } else {
                lock = new ReentrantLock();
                LOCKS_BY_PHONE.put(phone, lock);
            }
        }
        return lock;
    }
}

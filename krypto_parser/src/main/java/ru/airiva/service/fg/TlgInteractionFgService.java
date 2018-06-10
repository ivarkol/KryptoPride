package ru.airiva.service.fg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.parser.Courier;
import ru.airiva.service.da.TlgInteractionDaService;
import ru.airiva.tdlib.TdApi;
import ru.airiva.vo.TlgChannel;
import ru.airiva.vo.TlgChat;

import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

    @Autowired
    public void setTlgInteractionDaService(TlgInteractionDaService tlgInteractionDaService) {
        this.tlgInteractionDaService = tlgInteractionDaService;
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
    private TlgClient checkAuth(final String phone) throws TlgNeedAuthBsException, InterruptedException, TlgWaitAuthCodeBsException {
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
     */
    public void auth(final String phone) throws TlgWaitAuthCodeBsException, InterruptedException, TlgFailAuthBsException {


        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            try {
                //Проверяем текущий статус авторизации у клиента
                checkAuth(phone);
            } catch (TlgNeedAuthBsException e) {
                //Авторизовываем заново
                TlgClient tlgClient = new TlgClient(phone);
                TdApi.Object authStep = tlgClient.updatesHandler.authExchanger.exchange(null);
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
    public boolean checkCode(final String phone, final String code) throws TlgNeedAuthBsException, InterruptedException {
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

                TdApi.Object state = tlgClient.updatesHandler.checkCodeExchanger.exchange(null);
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
    public Collection<TlgChannel> getChannels(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
    public void enableParsing(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
    public void disableParsing(final String phone) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
    public void addCourier(final String phone, final Courier courier) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
    public void addCouriers(final String phone, final Collection<Courier> couriers) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
     * @param phone телефон клиента
     * @param courier курьер
     */
    public void deleteCourier(final String phone, final Courier courier) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
        Lock lockByPhone = getLockByPhone(phone);
        lockByPhone.lock();
        try {
            TlgClient tlgClient = checkAuth(phone);
            tlgClient.updatesHandler.dispatcher.deleteCourier(courier);
        } finally {
            lockByPhone.unlock();
        }
    }

    /**
     * Удаление набора курьеров из диспетчера
     *
     * @param phone телефон клиента
     * @param couriers коллекция курьеров
     */
    public void deleteCouriers(final String phone, final Collection<Courier> couriers) throws InterruptedException, TlgWaitAuthCodeBsException, TlgNeedAuthBsException {
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
     * Добавление курьеров в диспетчер из БД
     *
     * @param tlgClient текущий клиент
     */
    private void initializeCouriers(TlgClient tlgClient) {
        if (tlgClient == null) return;
        List<Courier> couriers = new ArrayList<>(); //TODO формировать из БД
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
                                    chatsBarrier.await();
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                    chatsBarrier.reset();
                                    LOGGER.error("Initialize chats was interrupted", e);
                                } catch (BrokenBarrierException e) {
                                    LOGGER.error("Chats initialize barrier was broken", e);
                                }
                                break;
                            default:
                                LOGGER.error("Receive wrong response from TDLib: {}", object);
                        }
                    });
                }
            }
            try {
                chatsBarrier.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.error("Chats initialize was interrupted", e);
                chatsBarrier.reset();
            } catch (BrokenBarrierException e) {
                LOGGER.error("Chats initialize barrier was broken", e);
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

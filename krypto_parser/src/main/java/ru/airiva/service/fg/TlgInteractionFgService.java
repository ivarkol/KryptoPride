package ru.airiva.service.fg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.da.TlgInteractionDaService;
import ru.airiva.tdlib.TdApi;
import ru.airiva.vo.TlgChannel;
import ru.airiva.vo.TlgChat;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
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
     * Авторизация клиента
     *
     * @param phone номер телефона клиента в формате +7ХХХХХХХХХ
     * @return авторизованный клиент
     */
    public TlgClient auth(final String phone) throws TlgWaitAuthCodeBsException, InterruptedException, TlgFailAuthBsException {

        Lock lockByPhone = getLockByPhone(phone);

        lockByPhone.lock();
        final TlgClient tlgClient;
        try {
            if (CLIENTS.containsKey(phone)) {
                TlgClient checkTlgClient = CLIENTS.get(phone);
                //Запрашиваем статус авторизации клиента
                switch (tlgInteractionDaService.getAuthorizationState(checkTlgClient).getConstructor()) {
                    case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                        //Если клиент ждет код
                        throw new TlgWaitAuthCodeBsException();
                    case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                        //Если клиент авторизован
                        return checkTlgClient;
                    default:
                        //Если другие статусы, то авторизовываем заново
                        CLIENTS.remove(phone);
                        break;
                }
            }
            tlgClient = new TlgClient(phone);
            TdApi.Object authStep = tlgClient.updatesHandler.authExchanger.exchange(null);
            switch (authStep.getConstructor()) {
                case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                    CLIENTS.put(phone, tlgClient);
                    throw new TlgWaitAuthCodeBsException();
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    CLIENTS.put(phone, tlgClient);
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    tlgInteractionDaService.closeClient(tlgClient);
                    throw new TlgFailAuthBsException(((TdApi.Error) authStep).message);
            }
        } finally {
            lockByPhone.unlock();
        }

        return tlgClient;
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
        TlgClient tlgClient = CLIENTS.get(phone);
        if (tlgClient == null) throw new TlgNeedAuthBsException();
        try {
            TdApi.AuthorizationState authorizationState = tlgInteractionDaService.getAuthorizationState(tlgClient);
            switch (authorizationState.getConstructor()) {
                case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
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
                    break;
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    result = true;
                    break;
                default:
                    throw new TlgNeedAuthBsException();
            }
        } finally {
            tlgClient.updatesHandler.setFromWaitCodeState(false);
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
    public Collection<TlgChannel> getChannels(final String phone) {
        TlgClient tlgClient = CLIENTS.get(phone);
        if (!tlgClient.updatesHandler.chatsInitialized) {
            initializeChats(tlgClient);
        }
        Collection<TlgChannel> channels = tlgClient.channels.values();
        //Перекладываем названия и id чатов в каналы
        channels.forEach(tlgChannel -> {
            String title = tlgClient.updatesHandler.supergroupId2Title.get(tlgChannel.id);
            tlgChannel.setTitle(title != null && !title.isEmpty() ? title : "unknown");
            tlgChannel.setChatId(tlgClient.updatesHandler.supergroupId2ChatId.get(tlgChannel.id));
        });
        return channels;
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
     * @param phone номер телефона
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

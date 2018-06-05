package ru.airiva.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.tdlib.TdApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class TlgInteractionService implements TlgInteraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlgInteractionService.class);

    private static final Map<String, TlgClient> CLIENTS = new HashMap<>();

    /**
     * Блокировки по номеру телефона
     */
    private static final Map<String, Lock> LOCKS_BY_PHONE = new HashMap<>();


    @Override
    public void authorize(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException {
        auth(phone);
    }

    @Override
    public void start(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException {

        final TlgClient tlgClient;

        Lock startLock = getLockByPhone(phone);
        startLock.lock();
        try {
            if (CLIENTS.containsKey(phone)) {
                tlgClient = CLIENTS.get(phone);
            } else {
                tlgClient = auth(phone);
            }
        } finally {
            startLock.unlock();
        }

        //TODO parse messages

    }

    /**
     * Авторизация клиента
     *
     * @param phone номер телефона клиента в формате +7ХХХХХХХХХ
     * @return авторизованный клиент
     */
    private TlgClient auth(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException {

        Lock authLock = getLockByPhone(phone);

        authLock.lock();
        final TlgClient tlgClient;
        try {
            if (CLIENTS.containsKey(phone)) return CLIENTS.get(phone);
            tlgClient = new TlgClient(phone);
            int authStep;
            try {
                authStep = tlgClient.getUpdatesHandler().getAuthExchanger().exchange(null);
            } catch (InterruptedException e) {
                throw new TlgFailAuthBsException();
            }
            switch (authStep) {
                case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR:
                    throw new TlgWaitAuthCodeBsException();
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    CLIENTS.put(phone, tlgClient);
                    break;
            }
        } finally {
            authLock.unlock();
        }

        return tlgClient;
    }

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

    @Override
    public void checkCode(String code) {
//        tlgClient.getClient().send(
//                new TdApi.CheckAuthenticationCode(code, "", ""),
//                tlgClient.getAuthorizationRequestHandler());
//
//        try {
//            tlgClient.getStateReadyBarrier().await();
//        } catch (InterruptedException e) {
//            LOGGER.info("State ready barrier was interrupted", e);
//        } catch (BrokenBarrierException e) {
//            LOGGER.info("State ready barrier was broken", e);
//            throw new Exception("Authorization fail");
//        }

    }

    @Override
    public void logout() {
//        tlgClient.getClient().send(new TdApi.LogOut(), object -> LOGGER.info(object.toString()));
    }

    @Override
    public List<String> getChats() {
        List<String> chats = new ArrayList<>();
//        tlgClient.getClient().send(new TdApi.GetChats(Long.MAX_VALUE, 0, 100), object -> {
//            switch (object.getConstructor()) {
//                case TdApi.Error.CONSTRUCTOR:
//                    LOGGER.error("Receive an error for GetChats: {}", object);
//                    break;
//                case TdApi.Chats.CONSTRUCTOR:
//                    long[] chatIds = ((TdApi.Chats) object).chatIds;
//                    Arrays.stream(chatIds).forEach(value -> chats.add(String.valueOf(value)));
//                    break;
//                default:
//                    LOGGER.error("Receive wrong response from TDLib: {}", object);
//            }
//
//        });
        return chats;
    }

}

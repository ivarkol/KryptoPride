package ru.airiva.service.fg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.da.TlgInteractionDaService;
import ru.airiva.tdlib.TdApi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Ivan
 */
@Service
public class TlgInteractionFgService {

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

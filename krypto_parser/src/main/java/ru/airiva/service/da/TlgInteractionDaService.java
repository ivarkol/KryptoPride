package ru.airiva.service.da;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgTimeoutBsException;
import ru.airiva.properties.Timeouts;
import ru.airiva.tdlib.TdApi;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Ivan
 */
@Service
public class TlgInteractionDaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlgInteractionDaService.class);

    private Timeouts timeouts;

    @Autowired
    public void setTimeouts(Timeouts timeouts) {
        this.timeouts = timeouts;
    }

    /**
     * Получение статуса авторизации текущего клиента
     *
     * @param tlgClient клиент
     * @return статус авторизации
     */
    public TdApi.AuthorizationState getAuthorizationState(final TlgClient tlgClient) throws InterruptedException, TlgTimeoutBsException {
        final TdApi.AuthorizationState authorizationState;
        tlgClient.client.send(new TdApi.GetAuthorizationState(), tlgClient.authorizationCheckHandler);
        try {
            authorizationState = tlgClient.authorizationCheckHandler.getAuthCheckExchanger().exchange(null, timeouts.authCheck, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            throw new TlgTimeoutBsException("Истекло время ожидания проверки авторизации", e);
        }
        return authorizationState;
    }

    /**
     * Закрытие клиента
     *
     * @param tlgClient клиент
     */
    public void closeClient(final TlgClient tlgClient) {
        tlgClient.client.send(new TdApi.Close(), object -> {
            switch (object.getConstructor()) {
                case TdApi.Ok.CONSTRUCTOR:
                    LOGGER.debug("Client {} successfully closed", tlgClient.phone);
                    break;
                default:
                    LOGGER.error("Client {} not closed. Error: {} {}", tlgClient.phone, ((TdApi.Error) object).code, ((TdApi.Error) object).message);
                    break;
            }
        });
    }

}

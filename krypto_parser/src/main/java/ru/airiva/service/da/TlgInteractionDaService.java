package ru.airiva.service.da;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.tdlib.TdApi;

/**
 * @author Ivan
 */
@Service
public class TlgInteractionDaService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlgInteractionDaService.class);

    /**
     * Получение статуса авторизации текущего клиента
     *
     * @param tlgClient клиент
     * @return статус авторизации
     */
    public TdApi.AuthorizationState getAuthorizationState(final TlgClient tlgClient) throws InterruptedException {
        final TdApi.AuthorizationState authorizationState;
        tlgClient.client.send(new TdApi.GetAuthorizationState(), tlgClient.authorizationCheckHandler);
        authorizationState = tlgClient.authorizationCheckHandler.getAuthCheckExchanger().exchange(null);
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
                    LOGGER.info("Client {} successfully closed", tlgClient.phone);
                    break;
                case TdApi.Error.CONSTRUCTOR:
                    LOGGER.error("Client {} not closed. Error: {} {}", tlgClient.phone, ((TdApi.Error) object).code, ((TdApi.Error) object).message);
                    break;
            }
        });
    }

}

package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;

import java.util.concurrent.Exchanger;

/**
 * @author Ivan
 */
public class AuthorizationCheckHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCheckHandler.class);

    private final Exchanger<TdApi.AuthorizationState> authCheckExchanger = new Exchanger<>();

    public Exchanger<TdApi.AuthorizationState> getAuthCheckExchanger() {
        return authCheckExchanger;
    }

    @Override
    public void onResult(TdApi.Object object) {
        LOGGER.debug(object.toString());
        if (object instanceof TdApi.AuthorizationState) {
            try {
                authCheckExchanger.exchange(((TdApi.AuthorizationState) object));
            } catch (InterruptedException e) {
                LOGGER.debug("Authorization check was interrupted");
            }
        }
    }
}

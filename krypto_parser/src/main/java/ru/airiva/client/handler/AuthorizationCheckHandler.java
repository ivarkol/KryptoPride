package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.properties.Timeouts;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;
import ru.airiva.utils.SpringContext;

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Ivan
 */
public class AuthorizationCheckHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthorizationCheckHandler.class);

    private final Exchanger<TdApi.AuthorizationState> authCheckExchanger = new Exchanger<>();
    private Timeouts timeouts;

    public Exchanger<TdApi.AuthorizationState> getAuthCheckExchanger() {
        return authCheckExchanger;
    }

    public AuthorizationCheckHandler() {
        timeouts = SpringContext.getContext().getBean(Timeouts.class);
    }

    @Override
    public void onResult(TdApi.Object object) {
        LOGGER.debug(object.toString());
        if (object instanceof TdApi.AuthorizationState) {
            try {
                authCheckExchanger.exchange(((TdApi.AuthorizationState) object), timeouts.authCheck, SECONDS);
            } catch (InterruptedException e) {
                LOGGER.debug("Authorization check was interrupted");
            } catch (TimeoutException e) {
                LOGGER.warn("Authorization check waiting timeout elapsed");
            }
        }
    }
}

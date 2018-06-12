package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.client.TlgClient;
import ru.airiva.properties.Timeouts;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;
import ru.airiva.utils.SpringContext;

import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * @author Ivan
 */
public class CheckAuthenticationCodeHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAuthenticationCodeHandler.class);

    private final TlgClient tlgClient;
    private final Timeouts timeouts;

    public CheckAuthenticationCodeHandler(TlgClient tlgClient) {
        this.tlgClient = tlgClient;
        this.timeouts = SpringContext.getContext().getBean(Timeouts.class);
    }

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.Error.CONSTRUCTOR:
                LOGGER.error("Receive an error: {}", object);
                try {
                    tlgClient.updatesHandler.checkCodeExchanger.exchange(object, timeouts.codeCheck, SECONDS);
                } catch (InterruptedException e) {
                    LOGGER.info("CheckCodeExchanger was interrupted from Error case", e);
                } catch (TimeoutException e) {
                    LOGGER.warn("Authentication check code waiting timeout elapsed", e);
                }
                break;
            case TdApi.Ok.CONSTRUCTOR:
                // result is already received through UpdateAuthorizationState, nothing to do
                break;
            default:
                LOGGER.error("Receive wrong response from TDLib: {}", object);
        }

    }
}

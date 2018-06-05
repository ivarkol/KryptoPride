package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.client.TlgClient;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;

/**
 * @author Ivan
 */
public class CheckAuthenticationCodeHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckAuthenticationCodeHandler.class);

    private final TlgClient tlgClient;

    public CheckAuthenticationCodeHandler(TlgClient tlgClient) {
        this.tlgClient = tlgClient;
    }

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()) {
            case TdApi.Error.CONSTRUCTOR:
                LOGGER.error("Receive an error: {}", object);
                try {
                    tlgClient.updatesHandler.checkCodeExchanger.exchange(object);
                } catch (InterruptedException e) {
                    LOGGER.info("CheckCodeExchanger was interrupted from Error case", e);
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

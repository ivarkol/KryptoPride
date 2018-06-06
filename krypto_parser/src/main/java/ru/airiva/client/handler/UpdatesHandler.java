package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.client.TlgClient;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;

import java.util.concurrent.Exchanger;

import static java.lang.System.getProperty;

public class UpdatesHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatesHandler.class);

    private final TlgClient tlgClient;
    private final AuthorizationRequestHandler authorizationRequestHandler;

    public final Exchanger<TdApi.Object> authExchanger = new Exchanger<>();
    public final Exchanger<TdApi.Object> checkCodeExchanger = new Exchanger<>();

    private volatile boolean fromWaitCodeState = false;

    public void setFromWaitCodeState(boolean fromWaitCodeState) {
        this.fromWaitCodeState = fromWaitCodeState;
    }

    public UpdatesHandler(TlgClient tlgClient) {
        this.tlgClient = tlgClient;
        this.authorizationRequestHandler = new AuthorizationRequestHandler();
    }

    @Override
    public void onResult(TdApi.Object object) {
        LOGGER.info("Incoming update: {}", object.toString());
        switch (object.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                onAuthStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                break;
        }
    }

    private void onAuthStateUpdated(TdApi.AuthorizationState authorizationState) {
        LOGGER.info("AuthState: {}", authorizationState.getClass().getSimpleName());
        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = getProperty("java.io.tmpdir") + "/tdlib/" + tlgClient.phone.substring(1);
                parameters.useMessageDatabase = true;
                parameters.apiId = 94575;
                parameters.apiHash = "a3406de8d171bb422bb6ddf3bbd800e2";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.systemVersion = "Unknown";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;

                tlgClient.client.send(new TdApi.SetTdlibParameters(parameters), authorizationRequestHandler);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                tlgClient.client.send(new TdApi.CheckDatabaseEncryptionKey(), authorizationRequestHandler);
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                tlgClient.client.send(new TdApi.SetAuthenticationPhoneNumber(tlgClient.phone, false, false), authorizationRequestHandler);
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                try {
                    authExchanger.exchange(authorizationState);
                } catch (InterruptedException e) {
                    LOGGER.info("AuthExchanger was interrupted from code waiting step", e);
                }
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                if (fromWaitCodeState) {
                    try {
                        checkCodeExchanger.exchange(authorizationState);
                    } catch (InterruptedException e) {
                        LOGGER.info("CheckCodeExchanger was interrupted from state ready step", e);
                        Thread.currentThread().interrupt();
                    }
                } else {
                    try {
                        authExchanger.exchange(authorizationState);
                    } catch (InterruptedException e) {
                        LOGGER.info("AuthExchanger was interrupted from state ready step", e);
                        Thread.currentThread().interrupt();
                    }
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                LOGGER.debug("Client {} in logging out state", tlgClient.phone);
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                LOGGER.debug("Client {} in closing state", tlgClient.phone);
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                LOGGER.debug("Client {} successful closed", tlgClient.phone);
                break;
            default:
                LOGGER.error("Unsupported authorization state: {}", authorizationState);

        }
    }

    private class AuthorizationRequestHandler implements Client.ResultHandler {

        private final Logger logger = LoggerFactory.getLogger(AuthorizationRequestHandler.class);

        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    logger.error("Receive an error: {}", object);
                    try {
                        authExchanger.exchange(object);
                    } catch (InterruptedException e) {
                        LOGGER.error("AuthExchanger was interrupted from Error case", e);
                        Thread.currentThread().interrupt();
                    }
                    break;
                case TdApi.Ok.CONSTRUCTOR:
                    // result is already received through UpdateAuthorizationState, nothing to do
                    break;
                default:
                    logger.error("Receive wrong response from TDLib: {}", object);
            }
        }


    }


}

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

    private final Exchanger<Integer> authExchanger = new Exchanger<>();

    public Exchanger<Integer> getAuthExchanger() {
        return authExchanger;
    }

    public UpdatesHandler(TlgClient tlgClient) {
        this.tlgClient = tlgClient;
        this.authorizationRequestHandler = new AuthorizationRequestHandler();
    }

    @Override
    public void onResult(TdApi.Object object) {
        LOGGER.info("Incoming update: {}", object.getClass().getSimpleName());
        switch (object.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                onAuthStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                break;
            case TdApi.UpdateOption.CONSTRUCTOR:
                TdApi.UpdateOption updateOption = (TdApi.UpdateOption) object;
                LOGGER.info("UpdateOption {} {}", updateOption.name, updateOption.value);
                break;
        }
    }

    private void onAuthStateUpdated(TdApi.AuthorizationState authorizationState) {
        LOGGER.info("AuthState: {}", authorizationState.getClass().getSimpleName());
        switch (authorizationState.getConstructor()) {
            case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
                parameters.databaseDirectory = getProperty("java.io.tmpdir") + "/tdlib/" + tlgClient.getPhone().substring(1);
                parameters.useMessageDatabase = true;
                parameters.apiId = 94575;
                parameters.apiHash = "a3406de8d171bb422bb6ddf3bbd800e2";
                parameters.systemLanguageCode = "en";
                parameters.deviceModel = "Desktop";
                parameters.systemVersion = "Unknown";
                parameters.applicationVersion = "1.0";
                parameters.enableStorageOptimizer = true;

                tlgClient.getClient().send(new TdApi.SetTdlibParameters(parameters), authorizationRequestHandler);
                break;
            case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                tlgClient.getClient().send(new TdApi.CheckDatabaseEncryptionKey(), authorizationRequestHandler);
                break;
            case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                tlgClient.getClient().send(new TdApi.SetAuthenticationPhoneNumber(tlgClient.getPhone(), false, false), authorizationRequestHandler);
                break;
            }
            case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                try {
                    authExchanger.exchange(TdApi.AuthorizationStateWaitCode.CONSTRUCTOR);
                } catch (InterruptedException e) {
                    LOGGER.info("AuthExchanger was interrupted from code waiting step", e);
                }
                break;
            }
            case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                try {
                    authExchanger.exchange(TdApi.AuthorizationStateReady.CONSTRUCTOR);
                } catch (InterruptedException e) {
                    LOGGER.info("AuthExchanger was interrupted from state ready step", e);
                }
                break;
            case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
                break;
            case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
                break;
            default:
                LOGGER.error("Unsupported authorization state: {}", authorizationState);

        }
    }

    public class AuthorizationRequestHandler implements Client.ResultHandler {

        private final Logger logger = LoggerFactory.getLogger(AuthorizationRequestHandler.class);

        @Override
        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    logger.error("Receive an error: {}", object);
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

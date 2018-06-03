package ru.airiva.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;

import java.util.Objects;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static java.lang.System.getProperty;
import static ru.airiva.utils.TdLibUtil.loadLibTd;

public class TlgClient {


    private static final Logger LOGGER = LoggerFactory.getLogger(TlgClient.class);

    static {
        loadLibTd();
    }

    private final CyclicBarrier codeWaitBarrier = new CyclicBarrier(2);
    private final CyclicBarrier stateReadyBarrier = new CyclicBarrier(2);

    private final Client client;
    private final String phone;
    private final UpdatesHandler updatesHandler;

    public String getPhone() {
        return phone;
    }

    public Client getClient() {
        return client;
    }

    public UpdatesHandler.AuthorizationRequestHandler getAuthorizationRequestHandler() {
        return updatesHandler.authorizationRequestHandler;
    }

    public CyclicBarrier getCodeWaitBarrier() {
        return codeWaitBarrier;
    }

    public CyclicBarrier getStateReadyBarrier() {
        return stateReadyBarrier;
    }

    public TlgClient(String phone) {
        this.phone = phone;
        updatesHandler = new UpdatesHandler();
        client = Client.create(updatesHandler, null, null);
    }

    private class UpdatesHandler implements Client.ResultHandler {

        private final UpdatesHandler.AuthorizationRequestHandler authorizationRequestHandler = new UpdatesHandler.AuthorizationRequestHandler();

        @Override
        public void onResult(TdApi.Object object) {
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
                    parameters.databaseDirectory = getProperty("java.io.tmpdir") + "/tdlib";
                    parameters.useMessageDatabase = true;
                    parameters.apiId = 94575;
                    parameters.apiHash = "a3406de8d171bb422bb6ddf3bbd800e2";
                    parameters.systemLanguageCode = "en";
                    parameters.deviceModel = "Desktop";
                    parameters.systemVersion = "Unknown";
                    parameters.applicationVersion = "1.0";
                    parameters.enableStorageOptimizer = true;

                    client.send(new TdApi.SetTdlibParameters(parameters), authorizationRequestHandler);
                    break;
                case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                    client.send(new TdApi.CheckDatabaseEncryptionKey(), authorizationRequestHandler);
                    break;
                case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
                    client.send(new TdApi.SetAuthenticationPhoneNumber(phone, false, false), authorizationRequestHandler);
                    break;
                }
                case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
                    try {
                        codeWaitBarrier.await();
                    } catch (InterruptedException e) {
                        LOGGER.info("Code waiting barrier was interrupted", e);
                    } catch (BrokenBarrierException e) {
                        LOGGER.info("Code waiting barrier was broken", e);
                    }
                    break;
                }
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    try {
                        stateReadyBarrier.await();
                    } catch (InterruptedException e) {
                        LOGGER.info("State ready barrier was interrupted", e);
                    } catch (BrokenBarrierException e) {
                        LOGGER.info("State ready barrier was broken", e);
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

        private class AuthorizationRequestHandler implements Client.ResultHandler {
            @Override
            public void onResult(TdApi.Object object) {
                switch (object.getConstructor()) {
                    case TdApi.Error.CONSTRUCTOR:
                        LOGGER.error("Receive an error: {}", object);
                        codeWaitBarrier.reset();
                        stateReadyBarrier.reset();
                        break;
                    case TdApi.Ok.CONSTRUCTOR:
                        // result is already received through UpdateAuthorizationState, nothing to do
                        break;
                    default:
                        LOGGER.error("Receive wrong response from TDLib: {}", object);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgClient tlgClient = (TlgClient) o;
        return Objects.equals(phone, tlgClient.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(phone);
    }
}

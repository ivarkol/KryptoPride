package ru.airiva.client.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.airiva.client.TlgClient;
import ru.airiva.parser.Dispatcher;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;
import ru.airiva.vo.TlgChannel;
import ru.airiva.vo.TlgChat;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;

import static java.lang.System.getProperty;

public class UpdatesHandler implements Client.ResultHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(UpdatesHandler.class);

    private final TlgClient tlgClient;
    private final AuthorizationRequestHandler authorizationRequestHandler;

    public final Dispatcher dispatcher;
    public final Exchanger<TdApi.Object> authExchanger = new Exchanger<>();
    public final Exchanger<TdApi.Object> checkCodeExchanger = new Exchanger<>();
    public final CountDownLatch logoutLatch = new CountDownLatch(1);

    public final TreeSet<TlgChat> orderedChats = new TreeSet<>();
    public final ConcurrentHashMap<Long, TlgChat> chats = new ConcurrentHashMap<>();
    public volatile boolean chatsInitialized = false;
    private final ConcurrentHashMap<Long, Integer> chatId2SupergroupId = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, Long> supergroupId2ChatId = new ConcurrentHashMap<>();
    public final ConcurrentHashMap<Integer, String> supergroupId2Title = new ConcurrentHashMap<>();

    private volatile boolean fromWaitCodeState = false;

    public void setFromWaitCodeState(boolean fromWaitCodeState) {
        this.fromWaitCodeState = fromWaitCodeState;
    }

    public UpdatesHandler(TlgClient tlgClient) {
        this.tlgClient = tlgClient;
        this.authorizationRequestHandler = new AuthorizationRequestHandler();
        this.dispatcher = new Dispatcher();
    }

    private void setChatOrder(TlgChat tlgChat, long order) {
        synchronized (orderedChats) {
            orderedChats.remove(tlgChat);
            tlgChat.setOrder(order);
            orderedChats.add(tlgChat);
        }
    }

    @Override
    public void onResult(TdApi.Object object) {
        if (object.getConstructor() != TdApi.UpdateUserStatus.CONSTRUCTOR) {
            LOGGER.debug("Incoming update: {}", object.toString());
        }
        switch (object.getConstructor()) {
            case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                onAuthStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                break;
            case TdApi.UpdateNewChat.CONSTRUCTOR:
                TdApi.Chat chat = ((TdApi.UpdateNewChat) object).chat;
                if (!chatsInitialized) {
                    TlgChat tlgChat = new TlgChat(chat.id, chat.order);
                    chats.put(chat.id, tlgChat);
                    setChatOrder(tlgChat, chat.order);
                }
                if (chat.type.getConstructor() == TdApi.ChatTypeSupergroup.CONSTRUCTOR) {
                    TdApi.ChatTypeSupergroup typeSupergroup = ((TdApi.ChatTypeSupergroup) chat.type);
                    if (typeSupergroup.isChannel) {
                        chatId2SupergroupId.put(chat.id, typeSupergroup.supergroupId);
                        supergroupId2ChatId.put(typeSupergroup.supergroupId, chat.id);
                        supergroupId2Title.put(typeSupergroup.supergroupId, chat.title);
                    }

                }
                break;
            case TdApi.UpdateSupergroup.CONSTRUCTOR:
                TdApi.Supergroup supergroup = ((TdApi.UpdateSupergroup) object).supergroup;
                if (supergroup.isChannel) {
                    synchronized (tlgClient.channels) {
                        int statusConstructor = supergroup.status.getConstructor();
                        if (statusConstructor == TdApi.ChatMemberStatusBanned.CONSTRUCTOR
                                || statusConstructor == TdApi.ChatMemberStatusLeft.CONSTRUCTOR) {
                            tlgClient.channels.remove(supergroup.id);
                        } else {
                            TlgChannel tlgChannel = tlgClient.channels.get(supergroup.id);
                            if (tlgChannel != null) {
                                tlgChannel.setUsername(supergroup.username);
                            } else {
                                tlgChannel = new TlgChannel(supergroup.id);
                                tlgChannel.setUsername(supergroup.username);
                                tlgClient.channels.put(supergroup.id, tlgChannel);
                            }

                        }
                    }
                }
                break;
            case TdApi.UpdateChatOrder.CONSTRUCTOR:
                if (!chatsInitialized) {
                    TdApi.UpdateChatOrder updateChat = (TdApi.UpdateChatOrder) object;
                    TlgChat tlgChat = chats.get(updateChat.chatId);
                    setChatOrder(tlgChat, updateChat.order);
                }
                break;
            case TdApi.UpdateChatLastMessage.CONSTRUCTOR:
                if (!chatsInitialized) {
                    TdApi.UpdateChatLastMessage updateChat = (TdApi.UpdateChatLastMessage) object;
                    TlgChat tlgChat = chats.get(updateChat.chatId);
                    setChatOrder(tlgChat, updateChat.order);
                }
                break;
            case TdApi.UpdateChatIsPinned.CONSTRUCTOR:
                if (!chatsInitialized) {
                    TdApi.UpdateChatIsPinned updateChat = (TdApi.UpdateChatIsPinned) object;
                    TlgChat tlgChat = chats.get(updateChat.chatId);
                    setChatOrder(tlgChat, updateChat.order);
                }
                break;
            case TdApi.UpdateChatDraftMessage.CONSTRUCTOR:
                if (!chatsInitialized) {
                    TdApi.UpdateChatDraftMessage updateChat = (TdApi.UpdateChatDraftMessage) object;
                    TlgChat tlgChat = chats.get(updateChat.chatId);
                    setChatOrder(tlgChat, updateChat.order);
                }
                break;
            case TdApi.UpdateChatTitle.CONSTRUCTOR:
                TdApi.UpdateChatTitle updateChatTitle = (TdApi.UpdateChatTitle) object;
                Integer supergroupId = chatId2SupergroupId.get(updateChatTitle.chatId);
                if (supergroupId != null) {
                    synchronized (tlgClient.channels) {
                        tlgClient.channels.get(supergroupId).setTitle(updateChatTitle.title);
                        supergroupId2Title.put(supergroupId, updateChatTitle.title);
                    }
                }
                break;
            case TdApi.UpdateNewMessage.CONSTRUCTOR:
                try {
                    if (dispatcher.isEnabled()) {
                        dispatcher.dispatch(((TdApi.UpdateNewMessage) object).message, tlgClient.client);
                    }
                } catch (Exception e) {
                    LOGGER.error("Error while executing dispatch: " + object.toString(), e);
                }
                break;
        }
    }

    private void onAuthStateUpdated(TdApi.AuthorizationState authorizationState) {
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
                    Thread.currentThread().interrupt();
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
                logoutLatch.countDown();
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

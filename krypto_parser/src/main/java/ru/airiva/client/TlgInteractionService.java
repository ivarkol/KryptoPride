package ru.airiva.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.airiva.tdlib.TdApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;

@Service
public class TlgInteractionService implements TlgInteraction {

    private static final Logger LOGGER = LoggerFactory.getLogger(TlgInteractionService.class);

    private TlgClient tlgClient;

    @Override
    public void auth(String phone) throws Exception {
        tlgClient = new TlgClient(phone);
        try {
            tlgClient.getCodeWaitBarrier().await();
        } catch (InterruptedException e) {
            LOGGER.info("Code waiting barrier was interrupted", e);
        } catch (BrokenBarrierException e) {
            LOGGER.info("Code waiting barrier was broken", e);
            throw new Exception("Authorization fail");
        }
    }

    @Override
    public void checkCode(String code) throws Exception {
        tlgClient.getClient().send(
                new TdApi.CheckAuthenticationCode(code, "", ""),
                tlgClient.getAuthorizationRequestHandler());

        try {
            tlgClient.getStateReadyBarrier().await();
        } catch (InterruptedException e) {
            LOGGER.info("State ready barrier was interrupted", e);
        } catch (BrokenBarrierException e) {
            LOGGER.info("State ready barrier was broken", e);
            throw new Exception("Authorization fail");
        }

    }

    @Override
    public void logout() {
        tlgClient.getClient().send(new TdApi.LogOut(), object -> LOGGER.info(object.toString()));
    }

    @Override
    public List<String> getChats() {
        List<String> chats = new ArrayList<>();
        tlgClient.getClient().send(new TdApi.GetChats(Long.MAX_VALUE, 0, 100), object -> {
            switch (object.getConstructor()) {
                case TdApi.Error.CONSTRUCTOR:
                    LOGGER.error("Receive an error for GetChats: {}", object);
                    break;
                case TdApi.Chats.CONSTRUCTOR:
                    long[] chatIds = ((TdApi.Chats) object).chatIds;
                    Arrays.stream(chatIds).forEach(value -> chats.add(String.valueOf(value)));
                    break;
                default:
                    LOGGER.error("Receive wrong response from TDLib: {}", object);
            }

        });
        return chats;
    }

}

package ru.airiva.service.cg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgCheckAuthCodeBsException;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.fg.TlgInteractionFgService;

import java.util.ArrayList;
import java.util.List;

@Service
public class TlgInteractionCgService implements TlgInteraction {

    private TlgInteractionFgService tlgInteractionFgService;

    @Autowired
    public void setTlgInteractionFgService(TlgInteractionFgService tlgInteractionFgService) {
        this.tlgInteractionFgService = tlgInteractionFgService;
    }

    @Override
    public void authorize(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException {
        try {
            tlgInteractionFgService.auth(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgFailAuthBsException();
        }
    }

    @Override
    public void start(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException {

        //Проверяем авторизацию клиента
        final TlgClient tlgClient;
        try {
            tlgClient = tlgInteractionFgService.auth(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgFailAuthBsException();
        }


        //TODO parse messages

    }

    @Override
    public boolean checkCode(final String phone, final String code) throws TlgNeedAuthBsException, TlgCheckAuthCodeBsException {
        boolean result;
        try {
            result = tlgInteractionFgService.checkCode(phone, code);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgCheckAuthCodeBsException();
        }
        return result;
    }

    @Override
    public void logout() {
//        tlgClient.getClient().send(new TdApi.LogOut(), object -> LOGGER.info(object.toString()));
    }

    @Override
    public List<String> getChats() {
        List<String> chats = new ArrayList<>();
//        tlgClient.getClient().send(new TdApi.GetChats(Long.MAX_VALUE, 0, 100), object -> {
//            switch (object.getConstructor()) {
//                case TdApi.Error.CONSTRUCTOR:
//                    LOGGER.error("Receive an error for GetChats: {}", object);
//                    break;
//                case TdApi.Chats.CONSTRUCTOR:
//                    long[] chatIds = ((TdApi.Chats) object).chatIds;
//                    Arrays.stream(chatIds).forEach(value -> chats.add(String.valueOf(value)));
//                    break;
//                default:
//                    LOGGER.error("Receive wrong response from TDLib: {}", object);
//            }
//
//        });
        return chats;
    }

}

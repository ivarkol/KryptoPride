package ru.airiva.service.cg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.client.TlgClient;
import ru.airiva.exception.TlgCheckAuthCodeBsException;
import ru.airiva.exception.TlgFailAuthBsException;
import ru.airiva.exception.TlgNeedAuthBsException;
import ru.airiva.exception.TlgWaitAuthCodeBsException;
import ru.airiva.service.fg.TlgInteractionFgService;
import ru.airiva.vo.TlgChannel;

import java.util.ArrayList;
import java.util.Collections;
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
    public void logout(final String phone) {
        tlgInteractionFgService.logout(phone);
    }

    @Override
    public List<TlgChannel> getSortedChannels(String phone) {
        List<TlgChannel> channels = new ArrayList<>(tlgInteractionFgService.getChannels(phone));
        Collections.sort(channels);
        return channels;
    }

}

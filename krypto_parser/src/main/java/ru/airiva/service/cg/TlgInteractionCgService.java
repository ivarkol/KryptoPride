package ru.airiva.service.cg;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.airiva.exception.*;
import ru.airiva.parser.Courier;
import ru.airiva.parser.Expression;
import ru.airiva.parser.Parser;
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
    public void authorize(final String phone) throws TlgWaitAuthCodeBsException, TlgFailAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.auth(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public boolean checkCode(final String phone, final String code) throws TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        boolean result;
        try {
            result = tlgInteractionFgService.checkCode(phone, code);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
        return result;
    }

    @Override
    public void startParsing(final String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {

        try {
            tlgInteractionFgService.enableParsing(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }

    }

    @Override
    public void stopParsing(String phone) throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.disableParsing(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public void logout(final String phone) {
        tlgInteractionFgService.logout(phone);
    }

    @Override
    public List<TlgChannel> getSortedChannels(String phone)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        List<TlgChannel> channels;
        try {
            channels = new ArrayList<>(tlgInteractionFgService.getChannels(phone));
            Collections.sort(channels);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
        return channels;
    }

    @Override
    public void includeParsing(String phone, long source, long target, long delay)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.addCourier(phone, new Courier(source, target, Parser.create(phone, source), delay));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public void excludeParsing(String phone, long source, long target)
            throws TlgWaitAuthCodeBsException, TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.deleteCourier(phone, Courier.template(source, target));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public void setMessageSendingDelay(String phone, long source, long target, long delay)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.setCourierDelay(phone, Courier.template(source, target), delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public void addParsingExpression(String phone, long source, long target, String search, String replacement, int order)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.addExpression(phone, Courier.template(source, target), new Expression(search, replacement, order));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public void removeParsingExpression(String phone, long source, long target, String search, String replacement)
            throws TlgNeedAuthBsException, TlgWaitAuthCodeBsException, TlgDefaultBsException, TlgTimeoutBsException {
        try {
            tlgInteractionFgService.removeExpression(phone, Courier.template(source, target), Expression.template(search, replacement));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
    }

    @Override
    public String resendCode(String phone) throws TlgNeedAuthBsException, TlgDefaultBsException, TlgTimeoutBsException {
        String codeType;
        try {
            codeType = tlgInteractionFgService.resendCode(phone);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TlgDefaultBsException(e);
        }
        return codeType;
    }
}

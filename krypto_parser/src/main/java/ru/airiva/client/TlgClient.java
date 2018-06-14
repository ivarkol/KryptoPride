package ru.airiva.client;

import ru.airiva.client.handler.AuthorizationCheckHandler;
import ru.airiva.client.handler.CheckAuthenticationCodeHandler;
import ru.airiva.client.handler.UpdatesHandler;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.Log;
import ru.airiva.vo.TlgChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static ru.airiva.utils.TdLibUtil.loadLibTd;

/**
 * Обертка для {@link Client}
 *
 * @author Ivan
 */
public class TlgClient {

    static {
        loadLibTd();
        //Disable log verbosity
        Log.setVerbosityLevel(0);
    }

    public final UpdatesHandler updatesHandler;
    public final AuthorizationCheckHandler authorizationCheckHandler;
    public final CheckAuthenticationCodeHandler checkAuthenticationCodeHandler;
    public final Client client;
    public final String phone;

    public final Map<Integer, TlgChannel> channels = new HashMap<>();

    public TlgClient(String phone) {
        this.phone = phone;
        updatesHandler = new UpdatesHandler(this);
        authorizationCheckHandler = new AuthorizationCheckHandler();
        checkAuthenticationCodeHandler = new CheckAuthenticationCodeHandler(this);
        client = Client.create(this.phone, updatesHandler, null, null);
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

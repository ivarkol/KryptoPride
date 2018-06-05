package ru.airiva.client;

import ru.airiva.client.handler.UpdatesHandler;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.Log;

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

    private final Client client;
    private final String phone;
    private final UpdatesHandler updatesHandler;

    public String getPhone() {
        return phone;
    }

    public Client getClient() {
        return client;
    }

    public UpdatesHandler getUpdatesHandler() {
        return updatesHandler;
    }

    public TlgClient(String phone) {
        this.phone = phone;
        updatesHandler = new UpdatesHandler(this);
        client = Client.create(updatesHandler, null, null);
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

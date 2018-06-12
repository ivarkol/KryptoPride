package ru.airiva.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import ru.airiva.tdlib.Client;
import ru.airiva.tdlib.TdApi;

import java.util.Objects;

/**
 * @author Ivan
 */
public class Courier {

    private static final Logger LOGGER = LoggerFactory.getLogger(Courier.class);

    /**
     * Идентификатор чата-источника сообщения
     */
    public final long source;
    /**
     * Идентификатор чата-получателя сообщения
     */
    public final long target;
    /**
     * Задержка перед отправкой сообщения
     */
    private volatile long delay;

    public final Parser parser;

    public void setDelay(long delay) {
        this.delay = delay*1000;
    }

    public Courier(long source, long target, Parser parser, long delay) {
        this.source = source;
        this.target = target;
        this.parser = parser;
        this.delay = delay*1000;
    }

    public static Courier template(long source, long target) {
        return new Courier(source, target, null, 0);
    }

    /**
     * Отправка сообщения
     *
     * @param message текст сообщения
     * @param client клиент, с помощью которого нужно отправить
     */
    @Async
    public void send(final String message, final Client client) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOGGER.error("Message sending was interrupted:");
            LOGGER.error("Message: {}", message);
            LOGGER.error("From: {}", source);
            LOGGER.error("To: {}", target);
        }
        final String parsedMessage = parser.parse(message);
        TdApi.InputMessageText inputMessageText = new TdApi.InputMessageText(
                new TdApi.FormattedText(parsedMessage, null),
                false,
                true);
        client.send(new TdApi.SendMessage(target, 0, false, false, null, inputMessageText), object -> {
            switch (object.getConstructor()) {
                case TdApi.Message.CONSTRUCTOR:
                    LOGGER.debug("Message is derived: {}", object.toString());
                    break;
            }
        });
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Courier courier = (Courier) o;
        return source == courier.source &&
                target == courier.target;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target);
    }
}

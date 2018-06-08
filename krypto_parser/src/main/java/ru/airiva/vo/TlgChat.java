package ru.airiva.vo;

/**
 * @author Ivan
 */
public class TlgChat implements Comparable<TlgChat> {

    public final long chatId;
    private long order;

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public TlgChat(long chatId, long order) {
        this.chatId = chatId;
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgChat tlgChat = (TlgChat) o;
        return chatId == tlgChat.chatId &&
                order == tlgChat.order;
    }

    @Override
    public int compareTo(TlgChat o) {
        if (this.order != o.order) {
            return this.order < o.order ? -1 : 1;
        }
        if (this.chatId != o.chatId) {
            return this.chatId < o.chatId ? -1 : 1;
        }
        return 0;
    }

}

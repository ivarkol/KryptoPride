package ru.airiva.entities;

import javax.persistence.*;

import java.util.Objects;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_CHATS)
public class TlgChatEntity {

    @Id
    @Column(name = "tlg_chat_id")
    private Long tlgChatId;

    @Column(name = "title")
    private String title;

    @Column(name = "username")
    private String username;

    @Column(name = "is_channel")
    private boolean channel = false;

    public Long getTlgChatId() {
        return tlgChatId;
    }

    public void setTlgChatId(Long tlgChatId) {
        this.tlgChatId = tlgChatId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isChannel() {
        return channel;
    }

    public void setChannel(boolean channel) {
        this.channel = channel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgChatEntity that = (TlgChatEntity) o;
        return Objects.equals(tlgChatId, that.tlgChatId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tlgChatId);
    }
}

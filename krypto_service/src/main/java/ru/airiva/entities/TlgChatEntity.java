package ru.airiva.entities;

import javax.persistence.*;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_CHAT)
@SequenceGenerator(name = TLG_CHAT_GEN, sequenceName = TLG_CHAT_SEQ, allocationSize = 1)
public class TlgChatEntity {

    @Id
    @GeneratedValue(generator = TLG_CHAT_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "chat_id")
    private long chatId;

    @Column(name = "title")
    private String title;

    @Column(name = "username")
    private String username;

    @Column(name = "is_channel")
    private boolean channel = false;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getChatId() {
        return chatId;
    }

    public void setChatId(long chatId) {
        this.chatId = chatId;
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
}

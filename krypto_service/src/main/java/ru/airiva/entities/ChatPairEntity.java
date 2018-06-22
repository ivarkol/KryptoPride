package ru.airiva.entities;

import javax.persistence.*;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = CHAT_PAIR)
@SequenceGenerator(name = CHAT_PAIR_GEN, sequenceName = CHAT_PAIR_SEQ, allocationSize = 1)
public class ChatPairEntity {

    @Id
    @GeneratedValue(generator = CHAT_PAIR_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "src_chat")
    private TlgChatEntity srcChat;

    @OneToOne
    @JoinColumn(name = "dest_chat")
    private TlgChatEntity destChat;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TlgChatEntity getSrcChat() {
        return srcChat;
    }

    public void setSrcChat(TlgChatEntity srcChat) {
        this.srcChat = srcChat;
    }

    public TlgChatEntity getDestChat() {
        return destChat;
    }

    public void setDestChat(TlgChatEntity destChat) {
        this.destChat = destChat;
    }
}

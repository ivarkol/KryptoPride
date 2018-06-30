package ru.airiva.entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = CHAT_PAIRS)
@SequenceGenerator(name = CHAT_PAIRS_GEN, sequenceName = CHAT_PAIRS_SEQ, allocationSize = 1)
public class TlgChatPairEntity {

    @Id
    @GeneratedValue(generator = CHAT_PAIRS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "src_chat_id")
    private TlgChatEntity srcChat;

    @OneToOne
    @JoinColumn(name = "dest_chat_id")
    private TlgChatEntity destChat;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_pair_id")
    private Set<OrderedExpressionEntity> orderedExpressionEntities;

    @Column(name = "delay")
    private long delay = 0;

    @ManyToOne
    @JoinColumn(name = "tr_package_id")
    private TlgTrPackageEntity tlgTrPackageEntity;

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

    public Set<OrderedExpressionEntity> getOrderedExpressionEntities() {
        return orderedExpressionEntities;
    }

    public void setOrderedExpressionEntities(Set<OrderedExpressionEntity> orderedExpressionEntities) {
        this.orderedExpressionEntities = orderedExpressionEntities;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    public TlgTrPackageEntity getTlgTrPackageEntity() {
        return tlgTrPackageEntity;
    }

    public void setTlgTrPackageEntity(TlgTrPackageEntity tlgTrPackageEntity) {
        this.tlgTrPackageEntity = tlgTrPackageEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgChatPairEntity that = (TlgChatPairEntity) o;
        return Objects.equals(srcChat, that.srcChat) &&
                Objects.equals(destChat, that.destChat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(srcChat, destChat);
    }
}

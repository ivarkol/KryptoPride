package ru.airiva.entities;

import javax.persistence.*;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_ADS)
@SequenceGenerator(name = TLG_ADS_GEN, sequenceName = TLG_ADS_SEQ, allocationSize = 1)
public class TlgAdEntity {

    @Id
    @GeneratedValue(generator = TLG_ADS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "tlg_client_id")
    private TlgClientEntity tlgClientEntity;

    @OneToOne
    @JoinColumn(name = "tlg_chat_id")
    private TlgChatEntity tlgChatEntity;

    @OneToOne
    @JoinColumn(name = "tlg_ad_message_id")
    private TlgAdMessageEntity tlgAdMessageEntity;

    @Column(name = "cron")
    private String cron;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TlgClientEntity getTlgClientEntity() {
        return tlgClientEntity;
    }

    public void setTlgClientEntity(TlgClientEntity tlgClientEntity) {
        this.tlgClientEntity = tlgClientEntity;
    }

    public TlgChatEntity getTlgChatEntity() {
        return tlgChatEntity;
    }

    public void setTlgChatEntity(TlgChatEntity tlgChatEntity) {
        this.tlgChatEntity = tlgChatEntity;
    }

    public TlgAdMessageEntity getTlgAdMessageEntity() {
        return tlgAdMessageEntity;
    }

    public void setTlgAdMessageEntity(TlgAdMessageEntity tlgAdMessageEntity) {
        this.tlgAdMessageEntity = tlgAdMessageEntity;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}

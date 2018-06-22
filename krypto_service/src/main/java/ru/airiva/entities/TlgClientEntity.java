package ru.airiva.entities;

import javax.persistence.*;

import java.util.Set;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TELEGRAM_CLIENT)
@SequenceGenerator(name = TELEGRAM_CLIENT_GEN, sequenceName = TELEGRAM_CLIENT_SEQ, allocationSize = 1)
public class TlgClientEntity {

    @Id
    @GeneratedValue(generator = TELEGRAM_CLIENT_GEN, strategy = GenerationType.SEQUENCE)
    public Long id;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "own_chats")
    private Set<TlgChatEntity> ownChats;

    @Column(name = "guest_chats")
    private Set<TlgChatEntity> guestChats;






}

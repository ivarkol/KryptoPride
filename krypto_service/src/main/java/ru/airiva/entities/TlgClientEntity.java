package ru.airiva.entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.TLG_CLIENTS;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_CLIENTS)
public class TlgClientEntity {

    @Id
    @Column(name = "tlg_id")
    private Long tlgId;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "username")
    private String username;

    @ManyToMany
    @JoinTable(
            name = "tlg_clients_own_chats",
            joinColumns = {@JoinColumn(name = "tlg_client_id")},
            inverseJoinColumns = {@JoinColumn(name = "own_chat_id")}
    )
    private Set<TlgChatEntity> ownChats;

    @ManyToMany
    @JoinTable(
            name = "tlg_clients_guest_chats",
            joinColumns = {@JoinColumn(name = "tlg_client_id")},
            inverseJoinColumns = {@JoinColumn(name = "guest_chat_id")}
    )
    private Set<TlgChatEntity> guestChats;

    public Long getTlgId() {
        return tlgId;
    }

    public void setTlgId(Long tlgId) {
        this.tlgId = tlgId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Set<TlgChatEntity> getOwnChats() {
        return ownChats;
    }

    public void setOwnChats(Set<TlgChatEntity> ownChats) {
        this.ownChats = ownChats;
    }

    public Set<TlgChatEntity> getGuestChats() {
        return guestChats;
    }

    public void setGuestChats(Set<TlgChatEntity> guestChats) {
        this.guestChats = guestChats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgClientEntity that = (TlgClientEntity) o;
        return Objects.equals(tlgId, that.tlgId);
    }

    @Override
    public int hashCode() {

        return Objects.hash(tlgId);
    }
}

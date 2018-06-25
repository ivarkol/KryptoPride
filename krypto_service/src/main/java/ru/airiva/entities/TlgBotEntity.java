package ru.airiva.entities;

import javax.persistence.*;

import java.util.Set;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_BOTS)
@SequenceGenerator(name = TLG_BOTS_GEN, sequenceName = TLG_BOTS_SEQ, allocationSize = 1)
public class TlgBotEntity {

    @Id
    @GeneratedValue(generator = TLG_BOTS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "tlg_bots_users",
            joinColumns = {@JoinColumn(name = "tlg_bot_id")},
            inverseJoinColumns = {@JoinColumn(name = "tlg_user_id")}
    )
    private Set<TlgUserEntity> tlgUserEntities;

    @Column(name = "token")
    private String token;

    @Column(name = "username")
    private String username;

    @OneToOne
    @JoinColumn(name = "person_id")
    private PersonEntity personEntity;

    @OneToMany
    @JoinColumn(name = "bot_id")
    private Set<TlgBotTranslationEntity> tlgBotTranslationEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TlgUserEntity> getTlgUserEntities() {
        return tlgUserEntities;
    }

    public void setTlgUserEntities(Set<TlgUserEntity> tlgUserEntities) {
        this.tlgUserEntities = tlgUserEntities;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public PersonEntity getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(PersonEntity personEntity) {
        this.personEntity = personEntity;
    }

    public Set<TlgBotTranslationEntity> getTlgBotTranslationEntities() {
        return tlgBotTranslationEntities;
    }

    public void setTlgBotTranslationEntities(Set<TlgBotTranslationEntity> tlgBotTranslationEntities) {
        this.tlgBotTranslationEntities = tlgBotTranslationEntities;
    }
}

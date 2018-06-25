package ru.airiva.entities;

import ru.airiva.enums.KryptoRole;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */

@Entity
@Table(name = PERSONS)
@SequenceGenerator(name = PERSONS_GEN, sequenceName = PERSONS_SEQ, allocationSize = 1)
public class PersonEntity {

    @Id
    @GeneratedValue(generator = PERSONS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @OneToMany
    @JoinColumn(name = "person_id")
    private Set<TlgClientEntity> clients;

    @OneToOne
    @JoinColumn(name = "bot_id")
    private TlgBotEntity bot;

    @OneToMany
    @JoinColumn(name = "person_id")
    private Set<TlgTrPackageEntity> tlgTrPackageEntities;

    @Column(name = "payment_address")
    private String paymentAddress;

    @Column(name = "username", unique = true)
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private KryptoRole role;

    @Column(name = "subscription_end_time")
    private LocalDateTime subscriptionEndTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TlgClientEntity> getClients() {
        return clients;
    }

    public void setClients(Set<TlgClientEntity> clients) {
        this.clients = clients;
    }

    public TlgBotEntity getBot() {
        return bot;
    }

    public void setBot(TlgBotEntity bot) {
        this.bot = bot;
    }

    public Set<TlgTrPackageEntity> getTlgTrPackageEntities() {
        return tlgTrPackageEntities;
    }

    public void setTlgTrPackageEntities(Set<TlgTrPackageEntity> tlgTrPackageEntities) {
        this.tlgTrPackageEntities = tlgTrPackageEntities;
    }

    public String getPaymentAddress() {
        return paymentAddress;
    }

    public void setPaymentAddress(String paymentAddress) {
        this.paymentAddress = paymentAddress;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public KryptoRole getRole() {
        return role;
    }

    public void setRole(KryptoRole role) {
        this.role = role;
    }

    public LocalDateTime getSubscriptionEndTime() {
        return subscriptionEndTime;
    }

    public void setSubscriptionEndTime(LocalDateTime subscriptionEndTime) {
        this.subscriptionEndTime = subscriptionEndTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonEntity that = (PersonEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

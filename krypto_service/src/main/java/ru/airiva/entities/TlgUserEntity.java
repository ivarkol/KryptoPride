package ru.airiva.entities;

import ru.airiva.enums.Locale;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.TLG_USERS;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_USERS)
public class TlgUserEntity {

    @Id
    @Column(name = "tlg_id")
    private Long tlgId;

    @ManyToMany
    @JoinTable(
            name = "tlg_users_tr_packages",
            joinColumns = {@JoinColumn(name = "tlg_user_id")},
            inverseJoinColumns = {@JoinColumn(name = "tlg_tr_package_id")}
    )
    private Set<TlgTrPackageEntity> tlgTrPackageEntities;

    @Column(name = "subscription_end_time")
    private LocalDateTime subscriptionEndTime;

    @Column(name = "username")
    private String username;

    @Column(name = "balance", scale = 2) //TODO в каких величинах и сколько десятичных знаков
    private BigDecimal balance;

    @Column(name = "locale")
    @Enumerated(EnumType.STRING)
    private Locale locale;

    public Long getTlgId() {
        return tlgId;
    }

    public void setTlgId(Long tlgId) {
        this.tlgId = tlgId;
    }

    public Set<TlgTrPackageEntity> getTlgTrPackageEntities() {
        return tlgTrPackageEntities;
    }

    public void setTlgTrPackageEntities(Set<TlgTrPackageEntity> tlgTrPackageEntities) {
        this.tlgTrPackageEntities = tlgTrPackageEntities;
    }

    public LocalDateTime getSubscriptionEndTime() {
        return subscriptionEndTime;
    }

    public void setSubscriptionEndTime(LocalDateTime subscriptionEndTime) {
        this.subscriptionEndTime = subscriptionEndTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}

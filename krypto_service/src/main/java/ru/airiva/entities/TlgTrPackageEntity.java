package ru.airiva.entities;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = TLG_TR_PACKAGES)
@SequenceGenerator(name = TLG_TR_PACKAGES_GEN, sequenceName = TLG_TR_PACKAGES_SEQ, allocationSize = 1)
public class TlgTrPackageEntity {

    @Id
    @GeneratedValue(generator = TLG_TR_PACKAGES_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToMany
    @JoinTable(
            name = "tlg_tr_packages_chat_pairs",
            joinColumns = {@JoinColumn(name = "tlg_tr_package_id")},
            inverseJoinColumns = {@JoinColumn(name = "chat_pair_id")}
    )
    private Set<TlgChatPairEntity> tlgChatPairEntities;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<TlgChatPairEntity> getTlgChatPairEntities() {
        return tlgChatPairEntities;
    }

    public void setTlgChatPairEntities(Set<TlgChatPairEntity> tlgChatPairEntities) {
        this.tlgChatPairEntities = tlgChatPairEntities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgTrPackageEntity that = (TlgTrPackageEntity) o;
        return Objects.equals(tlgChatPairEntities, that.tlgChatPairEntities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tlgChatPairEntities);
    }
}

package ru.airiva.entities;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Objects;
import java.util.Set;

import static ru.airiva.entities.EntityConstants.TLG_TR_PACKAGES;
import static ru.airiva.entities.EntityConstants.TLG_TR_PACKAGES_GEN;
import static ru.airiva.entities.EntityConstants.TLG_TR_PACKAGES_SEQ;

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


    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "tlg_tr_packages_chat_pairs",
            joinColumns = {@JoinColumn(name = "tlg_tr_package_id")},
            inverseJoinColumns = {@JoinColumn(name = "chat_pair_id")}
    )
    private Set<TlgChatPairEntity> tlgChatPairEntities;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonEntity personEntity;

    @Column(name = "enabled")
    private boolean enabled = false;

    @Column(name = "name")
    private String name;

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

    public PersonEntity getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(PersonEntity personEntity) {
        this.personEntity = personEntity;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TlgTrPackageEntity that = (TlgTrPackageEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

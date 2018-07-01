package ru.airiva.entities;

import javax.persistence.*;
import java.util.Objects;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = EXPRESSIONS)
@SequenceGenerator(name = EXPRESSION_GEN, sequenceName = EXPRESSION_SEQ, allocationSize = 1)
public class ExpressionEntity {

    @Id
    @GeneratedValue(generator = EXPRESSION_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "searchement", nullable = false)
    private String searchement;

    @Column(name = "replacement")
    private String replacement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id")
    private PersonEntity personEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSearchement() {
        return searchement;
    }

    public void setSearchement(String searchement) {
        this.searchement = searchement;
    }

    public String getReplacement() {
        return replacement;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public PersonEntity getPersonEntity() {
        return personEntity;
    }

    public void setPersonEntity(PersonEntity personEntity) {
        this.personEntity = personEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExpressionEntity that = (ExpressionEntity) o;
        return Objects.equals(searchement, that.searchement) &&
                Objects.equals(replacement, that.replacement) &&
                Objects.equals(personEntity, that.personEntity);
    }

    @Override
    public int hashCode() {

        return Objects.hash(searchement, replacement, personEntity);
    }
}

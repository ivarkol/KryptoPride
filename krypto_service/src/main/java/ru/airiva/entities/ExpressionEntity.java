package ru.airiva.entities;

import javax.persistence.*;

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

    @Column(name = "searchement")
    private String searchement;

    @Column(name = "replacement")
    private String replacement;

    @Column(name = "order")
    private int order = 0;

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

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}

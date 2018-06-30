package ru.airiva.entities;

import javax.persistence.*;
import java.util.Objects;

import static ru.airiva.entities.EntityConstants.*;

/**
 * @author Ivan
 */
@Entity
@Table(name = ORDERED_EXPRESSIONS)
@SequenceGenerator(name = ORDERED_EXPRESSIONS_GEN, sequenceName = ORDERED_EXPRESSIONS_SEQ, allocationSize = 1)
public class OrderedExpressionEntity {

    @Id
    @GeneratedValue(generator = ORDERED_EXPRESSIONS_GEN, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "order_value")
    private int order = 0;

    @ManyToOne
    @JoinColumn(name = "expression_id")
    private ExpressionEntity expressionEntity;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public ExpressionEntity getExpressionEntity() {
        return expressionEntity;
    }

    public void setExpressionEntity(ExpressionEntity expressionEntity) {
        this.expressionEntity = expressionEntity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderedExpressionEntity that = (OrderedExpressionEntity) o;
        return Objects.equals(expressionEntity, that.expressionEntity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expressionEntity);
    }

    public String getSearchement() {
        return expressionEntity != null ? expressionEntity.getSearchement() : null;
    }

    public String getReplacement() {
        return expressionEntity != null ? expressionEntity.getReplacement() : null;
    }
}

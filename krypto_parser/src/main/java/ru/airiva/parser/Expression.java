package ru.airiva.parser;

import java.util.Objects;

/**
 * @author Ivan
 */
public class Expression implements Comparable<Expression>{

    public final String search;
    public final String replacement;
    private int order;

    public void setOrder(int order) {
        this.order = order;
    }

    public Expression(String search, String replacement) {
        this(search, replacement, 0);
    }

    public Expression(String search, String replacement, int order) {
        this.search = search != null ? search : "";
        this.replacement = replacement != null ? replacement : "";
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expression that = (Expression) o;
        return Objects.equals(search, that.search) &&
                Objects.equals(replacement, that.replacement);
    }

    @Override
    public int hashCode() {
        return Objects.hash(search, replacement);
    }

    @Override
    public int compareTo(Expression o) {
        if (this.order != o.order) {
            return this.order < o.order ? -1 : 1;
        }
        return 0;
    }
}

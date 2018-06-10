package ru.airiva.parser;

import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * @author Ivan
 */
public class Parser {

    private final Set<Expression> expressions = new HashSet<>();
    private String[] searchList;
    private String[] replacementList;

    private Parser() {
    }

    public static Parser create(final String phone, final long source) {
        //TODO формировать из БД
        return new Parser();
    }

    public synchronized String parse(String message) {
        if (!expressions.isEmpty()) {
            message = StringUtils.replaceEach(message, getSearchList(), getReplacementList());
        }
        return message;
    }

    public synchronized void addExpression(Expression expression) {
        if (expression != null) {
            this.expressions.add(expression);
            reloadSearchList();
            reloadReplacementList();
        }
    }

    public synchronized void removeExpression(Expression expression) {
        if (expression != null) {
            this.expressions.remove(expression);
            reloadSearchList();
            reloadReplacementList();
        }
    }

    private String[] reloadSearchList() {
        List<Expression> expressions = new ArrayList<>(this.expressions);
        Collections.sort(expressions);
        String[] searchList = new String[expressions.size()];
        for (int i = 0; i < expressions.size(); i++) {
            searchList[i] = expressions.get(i).search;
        }
        this.searchList = searchList;
        return searchList;
    }

    private String[] reloadReplacementList() {
        List<Expression> expressions = new ArrayList<>(this.expressions);
        Collections.sort(expressions);
        String[] replacementList = new String[expressions.size()];
        for (int i = 0; i < expressions.size(); i++) {
            replacementList[i] = expressions.get(i).replacement;
        }
        this.replacementList = replacementList;
        return replacementList;
    }

    private String[] getSearchList() {
        String[] searchList;
        if (this.searchList != null) {
            searchList = this.searchList;
        } else {
            searchList = reloadSearchList();
        }
        return searchList;
    }

    private String[] getReplacementList() {
        String[] replacementList;
        if (this.replacementList != null) {
            replacementList = this.replacementList;
        } else {
            replacementList = reloadReplacementList();
        }
        return replacementList;
    }


}

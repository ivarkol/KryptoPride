package ru.airiva.parser;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Ivan
 */
public class Parser {

    private final Map<String, Expression> expressions = new CaseInsensitiveMap<>();
    private Pattern pattern;

    private Parser() {
    }

    /**
     * Создание парсера по данным из БД
     *
     * @param phone  телефон текущего клиента
     * @param source идентификатор канала источника
     * @return парсер
     */
    public static Parser create(final String phone, final long source) {
        //TODO формировать из БД
        return new Parser();
    }

    public synchronized String parse(String message) {
        if (!expressions.isEmpty()) {
            if (this.pattern == null) {
                this.pattern = patternCompile();
            }
            Matcher matcher = pattern.matcher(message);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                matcher.appendReplacement(sb, expressions.get(matcher.group()).replacement);
            }
            matcher.appendTail(sb);
            return sb.toString();
        } else {
            return message;
        }

    }

    public synchronized void addExpression(Expression expression) {
        if (expression != null && isNotBlank(expression.search)) {
            this.expressions.put(expression.search, expression);
            this.pattern = patternCompile();
        }
    }

    public synchronized void removeExpression(Expression expression) {
        if (expression != null && isNotBlank(expression.search)) {
            this.expressions.remove(expression.search);
            this.pattern = patternCompile();
        }
    }

    private Pattern patternCompile() {
        List<Expression> expressionList = new ArrayList<>(expressions.values());
        Collections.sort(expressionList);
        List<String> keyList = new ArrayList<>(expressionList.size());
        expressionList.forEach(expression -> keyList.add(quote(expression.search)));
        String patternString = "(?U)(?i)" + "(" + StringUtils.join(keyList, "|") + ")";
        return Pattern.compile(patternString);
    }
}

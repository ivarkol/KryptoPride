package ru.airiva.parser;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import ru.airiva.entities.OrderedExpressionEntity;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.quote;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author Ivan
 */
public class Parser {

    private final Map<String, Expression> expressions = new CaseInsensitiveMap<>();
    private Pattern pattern;

    /**
     * @param orderedExpressionEntities набор упорядоченных шаблонов
     */
    public Parser(final Set<OrderedExpressionEntity> orderedExpressionEntities) {
        if (isNotEmpty(orderedExpressionEntities)) {
            orderedExpressionEntities.forEach(exp -> expressions.put(exp.getSearchement(), new Expression(exp.getSearchement(), exp.getReplacement(), exp.getOrder())));
        }
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

package ru.airiva.parser;

import org.apache.commons.collections4.MapUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Constructor;
import java.util.Map;

import static org.apache.commons.lang3.reflect.FieldUtils.getField;
import static org.testng.Assert.*;

/**
 * @author Ivan
 */
public class ParserTest {

    private static final String TEST = "TEST";
    private Parser parser;

    @BeforeMethod
    public void setUp() throws Exception {
        Constructor<Parser> constructor = Parser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        parser = constructor.newInstance();
    }

    @Test
    public void testParseEmptyExpressions() {
        String message = TEST;
        String parsedMessage = parser.parse(message);
        assertNotNull(parsedMessage);
        assertEquals(parsedMessage, message);
    }

    @Test
    public void testParse() throws Exception{
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(parser.getClass(), "expressions", true).get(parser);
        Expression expr1 = new Expression("test", "тест", 0);
        Expression expr2 = new Expression("{", "(", 1);
        expressions.put(expr1.search, expr1);
        expressions.put(expr2.search, expr2);

        assertEquals(parser.parse("test"), "тест");
        assertEquals(parser.parse("TEST"), "тест");
        assertEquals(parser.parse("tesT"), "тест");
        assertEquals(parser.parse("{"), "(");
        assertEquals(parser.parse("testtest"), "тесттест");
        assertEquals(parser.parse("testTEST"), "тесттест");
        assertEquals(parser.parse("{{{{"), "((((");
        assertEquals(parser.parse("test{TEST{"), "тест(тест(");
        assertEquals(parser.parse("test{TE{ST{"), "тест(TE(ST(");
    }

    @Test
    public void testAddNullExpression() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(parser.getClass(), "expressions", true).get(parser);

        parser.addExpression(null);
        assertNotNull(expressions);
        assertTrue(expressions.isEmpty());

        parser.addExpression(new Expression(null, TEST, 0));
        assertNotNull(expressions);
        assertTrue(expressions.isEmpty());
    }

    @Test
    public void testAddExpression() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(parser.getClass(), "expressions", true).get(parser);

        Expression expression = new Expression(TEST, TEST, 0);
        parser.addExpression(expression);
        assertTrue(MapUtils.isNotEmpty(expressions));
        assertEquals(expressions.size(), 1);
        Expression addedExpression = expressions.get(expression.search);
        assertNotNull(addedExpression);
        assertEquals(addedExpression, expression);
    }

    @Test(dependsOnMethods = {"testAddExpression", "testAddNullExpression"})
    public void testRemoveNullExpression() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(parser.getClass(), "expressions", true).get(parser);

        parser.addExpression(new Expression(TEST, TEST, 0));
        assertEquals(expressions.size(), 1);

        parser.removeExpression(null);
        assertNotNull(expressions);
        assertEquals(expressions.size(), 1);

        parser.addExpression(Expression.template(null, TEST));
        assertNotNull(expressions);
        assertEquals(expressions.size(), 1);
    }

    @Test(dependsOnMethods = {"testAddExpression", "testAddNullExpression"})
    public void testRemoveExpression() throws Exception {
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(parser.getClass(), "expressions", true).get(parser);

        Expression expression = new Expression(TEST, TEST, 0);
        expressions.put(expression.search, expression);
        assertEquals(expressions.size(), 1);

        parser.removeExpression(Expression.template("TEST2", TEST));
        assertEquals(expressions.size(), 1);

        parser.removeExpression(Expression.template(expression.search, expression.replacement));
        assertEquals(expressions.size(), 0);
    }
}
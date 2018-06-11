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
public class DispatcherTest {

    private Dispatcher dispatcher;

    @BeforeMethod
    public void setUp() {
        dispatcher = new Dispatcher();
    }

    @Test
    public void testFindCourier() throws Exception {
        Constructor<Parser> constructor = Parser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Parser parser = constructor.newInstance();
        Expression expression = new Expression("test", "hello", 0);
        parser.addExpression(expression);

        long source = 1;
        long target = 2;
        Courier courier = new Courier(source, target, parser);
        dispatcher.putCourier(courier);

        Courier template = Courier.template(source, target);
        //test
        Courier findCourier = dispatcher.findCourier(template);

        //assertion
        assertNotNull(findCourier);
        assertEquals(findCourier.source, courier.source);
        assertEquals(findCourier.target, courier.target);
        Parser findParser = findCourier.parser;
        assertNotNull(findParser);
        @SuppressWarnings("unchecked")
        Map<String, Expression> expressions = (Map<String, Expression>) getField(findParser.getClass(), "expressions", true).get(findParser);
        assertTrue(MapUtils.isNotEmpty(expressions));
        assertEquals(expressions.size(), 1);
        Expression findExpression = expressions.get(expression.search);
        assertNotNull(findExpression);
        assertEquals(findExpression, expression);
    }

    @Test
    public void testNotFindCourier() throws Exception {
        Constructor<Parser> constructor = Parser.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        Parser parser = constructor.newInstance();
        Expression expression = new Expression("test", "hello", 0);
        parser.addExpression(expression);

        long source = 1;
        long target = 2;
        Courier courier = new Courier(source, target, parser);
        dispatcher.putCourier(courier);

        Courier template = Courier.template(source, 3);
        //test
        Courier findCourier = dispatcher.findCourier(template);

        //assertion
        assertNull(findCourier);
    }
}
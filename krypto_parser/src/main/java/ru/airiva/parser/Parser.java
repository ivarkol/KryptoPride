package ru.airiva.parser;

/**
 * @author Ivan
 */
public class Parser {

    private Parser() {
    }

    public String parse(String message) {
        return message;
    }

    public static Parser create(final String phone, final long sourse) {
        //TODO формировать из БД
        return new Parser();
    }

}

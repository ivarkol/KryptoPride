package ru.airiva.mapping.converter;

/**
 * @author Ivan
 */
public interface Converter<T, S> {

    S convert(T source);

}

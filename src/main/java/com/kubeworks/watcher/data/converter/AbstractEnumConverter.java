package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.AbstractEnum;

import javax.persistence.AttributeConverter;
import java.util.EnumSet;

public class AbstractEnumConverter<E extends Enum<E> & AbstractEnum<T>, T> implements AttributeConverter<E, T> {

    private final Class<E> attribute;

    public AbstractEnumConverter(Class<E> attribute) {
        this.attribute = attribute;
    }

    @Override
    public T convertToDatabaseColumn(E attribute) {
        return attribute.getValue();
    }

    @Override
    public E convertToEntityAttribute(T dbData) {
        return EnumSet.allOf(attribute).stream()
            .filter(type -> type.getValue().equals(dbData))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("not found value=" + dbData));
    }


}


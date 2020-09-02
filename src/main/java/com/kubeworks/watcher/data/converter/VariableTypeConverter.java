package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.data.vo.VariableType;

import javax.persistence.Converter;

@Converter(autoApply = true)
public class VariableTypeConverter extends AbstractEnumConverter<VariableType, String> {

    public VariableTypeConverter() {
        super(VariableType.class);
    }

}

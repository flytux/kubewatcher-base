package com.kubeworks.watcher.data.converter;

import com.kubeworks.watcher.ecosystem.ExternalConstants;
import io.kubernetes.client.custom.Quantity;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class QuantityConverter implements AttributeConverter<Quantity, String>  {

    @Override
    public String convertToDatabaseColumn(Quantity attribute) {
        return ExternalConstants.toStringQuantityViaK8s(attribute);
    }

    @Override
    public Quantity convertToEntityAttribute(String dbData) {
        return Quantity.fromString(dbData);
    }
}

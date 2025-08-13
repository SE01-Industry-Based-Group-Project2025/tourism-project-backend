package com.sl_tourpal.backend.domain;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class BooleanToBitConverter implements AttributeConverter<Boolean, Boolean> {
    @Override
    public Boolean convertToDatabaseColumn(Boolean attribute) {
        return attribute;
    }

    @Override
    public Boolean convertToEntityAttribute(Boolean dbData) {
        return dbData;
    }
}

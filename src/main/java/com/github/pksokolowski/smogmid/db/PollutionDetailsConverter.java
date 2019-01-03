package com.github.pksokolowski.smogmid.db;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PollutionDetailsConverter implements AttributeConverter<PollutionDetails, Integer> {
    @Override
    public Integer convertToDatabaseColumn(PollutionDetails pollutionDetails) {
        return pollutionDetails.encode();
    }

    @Override
    public PollutionDetails convertToEntityAttribute(Integer encoded) {
        return new PollutionDetails(encoded);
    }
}
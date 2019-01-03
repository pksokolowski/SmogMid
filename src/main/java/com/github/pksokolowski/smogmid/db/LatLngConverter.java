package com.github.pksokolowski.smogmid.db;

import com.github.pksokolowski.smogmid.utils.LatLng;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class LatLngConverter implements AttributeConverter<LatLng, String> {
    @Override
    public String convertToDatabaseColumn(LatLng latLng) {
        return latLng.getLatitude()+"/"+latLng.getLongitude();
    }

    @Override
    public LatLng convertToEntityAttribute(String s) {
        final var parts = s.split("/");
        final var lat = Double.parseDouble(parts[0]);
        final var lng = Double.parseDouble(parts[1]);

        return new LatLng(lat, lng);
    }
}

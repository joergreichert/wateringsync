package de.l.codefor.wateringsync.supabase.to;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;

@Singleton
@Converter(autoApply = true)
public class WateringConverterJson implements AttributeConverter<WateringTO, String> {
    private static ObjectMapper objectMapper;

    @Inject
    public void setObjectMapper(ObjectMapper objectMapper){
        WateringConverterJson.objectMapper = objectMapper;
    }

    @Override
    public String convertToDatabaseColumn(WateringTO watering) {
        try {
            return objectMapper.writeValueAsString(watering);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    @Override
    public WateringTO convertToEntityAttribute(String dbData) {
        try {
            return objectMapper.readValue(dbData, WateringTO.class);
        } catch (IOException ex) {
            return null;
        }
    }
}

package com.example.trafileatestproject.util;

import com.example.trafileatestproject.model.api.CategoryEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class CategoryEnumDeserializer extends JsonDeserializer<CategoryEnum> {
    @Override
    public CategoryEnum deserialize(JsonParser p, DeserializationContext deserializationContext) throws IOException {
        String categoryString = p.getValueAsString();
        return CategoryEnum.fromDescription(categoryString);
    }
}



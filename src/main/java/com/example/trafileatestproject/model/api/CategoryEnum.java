package com.example.trafileatestproject.model.api;

import com.example.trafileatestproject.util.CategoryEnumDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

@Getter
@JsonDeserialize(using = CategoryEnumDeserializer.class)
public enum CategoryEnum {
    COFFEE ("Coffee"),
    EQUIPMENT("Equipment"),
    ACCESSORIES("Accessories");

    private final String description;

    CategoryEnum(final String description) {
        this.description = description;
    }

    public static CategoryEnum fromDescription(String description) {
        for (CategoryEnum category : CategoryEnum.values()) {
            if (category.description.equalsIgnoreCase(description)) {
                return category;
            }
        }
        throw new IllegalArgumentException("Unknown category: " + description);
    }
}

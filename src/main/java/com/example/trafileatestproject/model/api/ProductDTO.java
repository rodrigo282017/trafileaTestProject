package com.example.trafileatestproject.model.api;

import com.example.trafileatestproject.model.entity.Category;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
@ToString
public class ProductDTO {
    private String name;
    private BigDecimal price;
    private Set<Category> categories;
}

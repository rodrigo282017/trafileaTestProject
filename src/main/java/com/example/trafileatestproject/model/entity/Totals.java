package com.example.trafileatestproject.model.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class Totals {
    private int products;
    private BigDecimal discounts;
    private BigDecimal shipping;
    private BigDecimal orderTotal;
}

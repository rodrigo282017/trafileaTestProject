package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class TotalsDTO {
    private int products;
    private BigDecimal discounts;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;
}

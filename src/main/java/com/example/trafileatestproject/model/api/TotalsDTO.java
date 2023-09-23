package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
public class TotalsDTO {
    private int products;
    private BigDecimal discounts;
    private BigDecimal shippingPrice;
    private BigDecimal totalPrice;
}

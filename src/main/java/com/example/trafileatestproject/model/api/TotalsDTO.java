package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class TotalsDTO {
    private int products;
    private double discounts;
    private double shipping;
    private double orderTotal;
}

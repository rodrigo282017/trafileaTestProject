package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@ToString
public class OrderDTO {
    private UUID id;
    private String cartID;
    private TotalsDTO totals;
}

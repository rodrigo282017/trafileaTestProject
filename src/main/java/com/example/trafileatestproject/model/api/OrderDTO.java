package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class OrderDTO {
    private Long id;
    private String cartID;
    private TotalsDTO totals;
}

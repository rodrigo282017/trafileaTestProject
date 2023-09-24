package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


import java.util.UUID;

@Data
@Builder
@ToString
public class OrderDTO {
    private UUID id;
    private CartDTO cart;
    private TotalsDTO totals;
}

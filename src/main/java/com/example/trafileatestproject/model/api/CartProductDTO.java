package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

@Data
@Builder
@ToString
public class CartProductDTO {
    private UUID cartId;
    private UUID productId;
    private int quantity;
}

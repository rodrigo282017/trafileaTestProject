package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@ToString
public class CartDTO {
    private UUID id;
    private UserDTO user;
    private Set<ProductQuantityDTO> productQuantity;
}

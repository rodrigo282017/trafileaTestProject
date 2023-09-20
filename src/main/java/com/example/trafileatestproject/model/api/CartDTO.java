package com.example.trafileatestproject.model.api;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
public class CartDTO {
    private UserDTO user;
    private List<ProductDTO> products;
}

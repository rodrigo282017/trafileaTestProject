package com.example.trafileatestproject.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Embeddable
public class CartProductId implements Serializable {
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "product_id")
    private UUID productId;
}

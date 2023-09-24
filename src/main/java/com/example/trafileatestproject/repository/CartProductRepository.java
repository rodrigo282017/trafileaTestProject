package com.example.trafileatestproject.repository;

import com.example.trafileatestproject.model.entity.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartProductRepository extends JpaRepository<CartProduct, UUID> {
    List<CartProduct> findAllByCartId(UUID cartId);

    Optional<CartProduct> findByCartIdAndProductId(UUID cartId, UUID productID);
}

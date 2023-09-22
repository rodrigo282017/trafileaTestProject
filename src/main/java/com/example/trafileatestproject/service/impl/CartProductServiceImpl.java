package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.entity.CartProduct;
import com.example.trafileatestproject.model.mapper.CartProductMapper;
import com.example.trafileatestproject.repository.CartProductRepository;
import com.example.trafileatestproject.service.ICartProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartProductServiceImpl implements ICartProductService {
    private final CartProductRepository cartProductRepository;

    @Override
    public CartProductDTO getCardProductByIDs(String cartId, String productId) {
        CartProduct cartProduct = cartProductRepository
                .findByCartIdAndProductId(UUID.fromString(cartId), UUID.fromString(productId))
                .orElseThrow(() -> new EntityNotFoundException
                        ("CartProduct not found for cart id: " + cartId + " and product id: " + productId));

        return new CartProductMapper().toDto(cartProduct);
    }
}

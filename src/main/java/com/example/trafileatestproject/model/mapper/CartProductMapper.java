package com.example.trafileatestproject.model.mapper;

import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.entity.CartProduct;
import com.example.trafileatestproject.util.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartProductMapper implements Mapper<CartProductDTO, CartProduct> {
    @Override
    public CartProductDTO toDto(CartProduct cartProduct) {
        return CartProductDTO.builder()
                .cartId(cartProduct.getId().getCartId())
                .productId(cartProduct.getId().getProductId())
                .quantity(cartProduct.getQuantity())
                .build();
    }

    @Override
    public List<CartProductDTO> toDTOs(List<CartProduct> cartProducts) {
        return cartProducts.stream().map(this::toDto).toList();
    }

    @Override
    public CartProduct toEntity(CartProductDTO cartProductDTO) {
        return null;
    }
}

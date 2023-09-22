package com.example.trafileatestproject.model.mapper;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.entity.Cart;
import com.example.trafileatestproject.util.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CartMapper implements Mapper<CartDTO, Cart> {
    @Override
    public CartDTO toDto(Cart cart) {
        return CartDTO.builder()
                .id(cart.getId())
                .user(new UserMapper().toDto(cart.getUser()))
                .build();
    }

    @Override
    public List<CartDTO> toDtos(List<Cart> carts) {
        return null;
    }

    @Override
    public Cart toEntity(CartDTO cartDTO) {
        return null;
    }
}

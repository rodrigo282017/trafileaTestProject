package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.api.ProductQuantityDTO;
import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.Cart;
import com.example.trafileatestproject.model.entity.CartProduct;
import com.example.trafileatestproject.model.entity.CartProductId;
import com.example.trafileatestproject.model.mapper.CartMapper;
import com.example.trafileatestproject.model.mapper.CartProductMapper;
import com.example.trafileatestproject.model.mapper.ProductMapper;
import com.example.trafileatestproject.model.mapper.UserMapper;
import com.example.trafileatestproject.repository.CartProductRepository;
import com.example.trafileatestproject.repository.CartRepository;
import com.example.trafileatestproject.service.ICartService;
import com.example.trafileatestproject.service.IProductService;
import com.example.trafileatestproject.service.IUserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {
    private final CartRepository cartRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final CartProductRepository cartProductRepository;

    @Override
    public CartDTO createEmptyCart(String userId) {
        UserDTO userDTO = userService.getUserById(userId);

        Cart createdCart = Cart.builder()
                .id(UUID.randomUUID())
                .user(new UserMapper().toEntity(userDTO))
                .build();

        Cart cart = cartRepository.save(createdCart);

        return new CartMapper().toDto(cart);
    }

    @Override
    public CartDTO addProducts(String id, ProductQuantityDTO productQuantityDTO) {
        //First get the cart created without products
        Cart cart = cartRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for this id: " + id));

        //Then check if the product exists
        ProductDTO productDTO = productService.getProductById(productQuantityDTO.getProductId().toString());

        //Create cartProduct Entity. The relation between the cart and the product with the quantity
        CartProductId cartProductId = CartProductId.builder()
                .productId(productDTO.getId())
                .cartId(cart.getId())
                .build();

        CartProduct cartProduct = CartProduct.builder()
                .id(cartProductId)
                .product(new ProductMapper().toEntity(productDTO))
                .cart(cart)
                .quantity(productQuantityDTO.getQuantity())
                .build();

        cartProductRepository.save(cartProduct);

        //Save the updated cart
        Cart updatedCart = cartRepository.save(cart);

        CartDTO cartDTO = new CartMapper().toDto(updatedCart);
        cartDTO.setProductQuantity(productQuantityDTO);

        return cartDTO;
    }

    @Override
    public CartProductDTO modifyProductQuantity(String id, String productId, int quantity) {
        //Check first if the product exists
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(UUID.fromString(id), UUID.fromString(productId))
                .orElseThrow(() ->
                        new EntityNotFoundException("Cart Product not found for this id: " + id + " and productId: " + productId));

        //Set the new quantity
        cartProduct.setQuantity(quantity);

        //Update the cart Product
        cartProductRepository.save(cartProduct);

        return new CartProductMapper().toDto(cartProduct);
    }

    @Override
    public CartDTO getCartById(String id) {
        Cart cart = cartRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for this id: " + id));

        return new CartMapper().toDto(cart);
    }
}

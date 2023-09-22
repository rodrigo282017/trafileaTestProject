package com.example.trafileatestproject.controller;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.api.ProductQuantityDTO;
import com.example.trafileatestproject.service.ICartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/carts")
public class CartController {
    private final ICartService cartService;

    @PutMapping("/{userId}")
    public ResponseEntity<CartDTO> createEmptyCart(@PathVariable String userId) {
        CartDTO createdCart = cartService.createEmptyCart(userId);

        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/products", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CartDTO> addProducts(@PathVariable String id, @RequestBody ProductQuantityDTO productQuantityDTO) {
        CartDTO createdCart = cartService.addProducts(id, productQuantityDTO);

        return new ResponseEntity<>(createdCart, HttpStatus.CREATED);
    }

    @PostMapping("/{id}/products/{productId}/modify-quantity")
    public ResponseEntity<CartProductDTO> modifyProductQuantity(@PathVariable String id,
                                                                @PathVariable String productId,
                                                                @RequestBody int quantity) {
        CartProductDTO modifiedCartProduct = cartService.modifyProductQuantity(id, productId, quantity);

        return new ResponseEntity<>(modifiedCartProduct, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CartDTO> getProductById(@PathVariable String id) {
        return ResponseEntity.ok(cartService.getCartById(id));
    }

}

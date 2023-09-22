package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.api.ProductQuantityDTO;

import java.util.List;

public interface ICartService {
    CartDTO createEmptyCart(String userId);

    CartDTO addProducts(String id, ProductQuantityDTO productQuantityDTO);

    CartProductDTO modifyProductQuantity(String id, String productId, int quantity);

    CartDTO getCartById(String id);
}

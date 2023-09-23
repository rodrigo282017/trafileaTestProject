package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.CartProductDTO;

public interface ICartProductService {
    CartProductDTO getCartProductByIDs(String cartId, String productId);
}

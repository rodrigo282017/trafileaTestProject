package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.ProductDTO;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    List<ProductDTO> getAllProducts();

    ProductDTO getProductById(String id);

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO updateProduct(String id, ProductDTO productDTO);

    void deleteProduct(String id);
}

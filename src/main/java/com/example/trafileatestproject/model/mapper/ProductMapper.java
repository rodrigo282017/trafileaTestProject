package com.example.trafileatestproject.model.mapper;

import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.entity.Product;
import com.example.trafileatestproject.util.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ProductMapper implements Mapper<ProductDTO, Product> {

    @Override
    public ProductDTO toDto(Product product) {
        return ProductDTO.builder()
                .categories(product.getCategories())
                .name(product.getName())
                .price(product.getPrice())
                .build();
    }

    @Override
    public List<ProductDTO> toDtos(List<Product> products) {
        return products.stream().map(this::toDto).toList();
    }

    @Override
    public Product toEntity(ProductDTO productDTO) {
        return Product.builder()
                .id(UUID.randomUUID())
                .name(productDTO.getName())
                .price(productDTO.getPrice())
                .categories(productDTO.getCategories())
                .build();
    }
}
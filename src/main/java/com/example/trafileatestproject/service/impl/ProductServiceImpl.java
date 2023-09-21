package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.entity.Product;
import com.example.trafileatestproject.model.mapper.ProductMapper;
import com.example.trafileatestproject.repository.ProductRepository;
import com.example.trafileatestproject.service.IProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> product = productRepository.findAll();

        return new ProductMapper().toDtos(product);
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Identity not found for this id: " + id));

        return new ProductMapper().toDto(product);
    }

    @Override
    public ProductDTO createProduct(ProductDTO productDTO) {
        productRepository.save(new ProductMapper().toEntity(productDTO));

        return productDTO;
    }

    @Override
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Optional<Product> optionalProduct = productRepository.findById(UUID.fromString(id));

        if (optionalProduct.isEmpty()) {
            throw new EntityNotFoundException("Identity not found for this id: " + id);
        }

        Product product = optionalProduct.get();

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }

        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }

        if (productDTO.getCategories() != null) {
            product.setCategories(productDTO.getCategories());
        }

        productRepository.save(product);

        return new ProductMapper().toDto(product);
    }

    @Override
    public void deleteProduct(String id) {
        productRepository.deleteById(UUID.fromString(id));
    }
}

package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.entity.Product;
import com.example.trafileatestproject.model.mapper.ProductMapper;
import com.example.trafileatestproject.repository.ProductRepository;
import com.example.trafileatestproject.service.IProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.util.StringUtils.hasText;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService {
    private final ProductRepository productRepository;

    @Override
    public List<ProductDTO> getAllProducts() {
        List<Product> productList = productRepository.findAll();

        return new ProductMapper().toDTOs(productList);
    }

    @Override
    public ProductDTO getProductById(String id) {
        Product product = productRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Product not found", "Could not find product.", id));

        return new ProductMapper().toDto(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        validateRequest(productDTO);
        Product product = productRepository.save(new ProductMapper().toEntity(productDTO));
        productDTO.setId(product.getId());

        return productDTO;
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(String id, ProductDTO productDTO) {
        Optional<Product> optionalProduct = productRepository.findById(UUID.fromString(id));

        if (optionalProduct.isEmpty()) {
            throw new EntityNotFoundException("Product not found", "Could not find product.", id);
        }

        Product product = optionalProduct.get();

        if (productDTO.getName() != null) {
            product.setName(productDTO.getName());
        }

        if (productDTO.getPrice() != null) {
            product.setPrice(productDTO.getPrice());
        }

        if (productDTO.getCategory() != null) {
            product.setCategory(productDTO.getCategory());
        }

        productRepository.save(product);

        return new ProductMapper().toDto(product);
    }

    @Override
    @Transactional
    public void deleteProduct(String id) {
        productRepository.deleteById(UUID.fromString(id));
    }

    private void validateRequest(ProductDTO productDTO) {
        if (!hasText(productDTO.getName()) || productDTO.getCategory() == null || productDTO.getPrice() == null) {
            throw new ValidationException(
                    "MissingRequiredParameters",
                    "Name, category and price are required",
                    productDTO.getName(),
                    productDTO.getCategory(),
                    productDTO.getPrice()
            );
        }
    }
}

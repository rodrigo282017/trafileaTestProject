package com.example.trafileatestproject.service;

import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.api.CategoryEnum;
import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.entity.Product;
import com.example.trafileatestproject.repository.ProductRepository;
import com.example.trafileatestproject.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    void createProduct_ValidProductDTO_ReturnsProductDTO() {
        ProductDTO productDTO = ProductDTO.builder()
                .name("Coffee machine")
                .price(BigDecimal.TEN)
                .category(CategoryEnum.COFFEE)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(new Product());

        // Act
        ProductDTO createdProduct = productService.createProduct(productDTO);

        // Assert
        assertEquals(productDTO.getName(), createdProduct.getName());
        assertEquals(productDTO.getPrice(), createdProduct.getPrice());
        assertEquals(productDTO.getCategory(), createdProduct.getCategory());
    }

    @Test
    void createProduct_InvalidProductDTO_ThrowsValidationException() {
        ProductDTO productDTO = ProductDTO.builder().build();

        ValidationException exception = assertThrows(ValidationException.class, () -> productService.createProduct(productDTO));
        assertEquals("MissingRequiredParameters", exception.getCode());
        assertEquals("Name, category and price are required", exception.getMessage());

    }

    @Test
    void getProductById_ExistingProductId_ReturnsProductDTO() {
        UUID productId = UUID.randomUUID();
        when(productRepository.findById(productId))
                .thenReturn(Optional.of(new Product()));

        ProductDTO productDTO = productService.getProductById(productId.toString());

        assertNotNull(productDTO);
    }

    @Test
    void getProductById_NonExistingProductId_ThrowsEntityNotFoundException() {
        String nonExistingId = UUID.randomUUID().toString();
        when(productRepository.findById(UUID.fromString(nonExistingId))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> productService.getProductById(nonExistingId));
    }

    @Test
    void updateProduct_InvalidProductDTO_ThrowsValidationException() {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .name("Updated Product Name")
                .category(CategoryEnum.COFFEE)
                .build();

        Product existingProduct = new Product();

        EntityNotFoundException exception = assertThrows
                (EntityNotFoundException.class, () -> productService.updateProduct(productId.toString(), productDTO));

        assertEquals("Product not found", exception.getCode());
        assertEquals("Could not find product.", exception.getMessage());
    }

    @Test
    void updateProduct_ValidProductDTO_ReturnsUpdatedProductDTO() {
        UUID productId = UUID.randomUUID();
        ProductDTO productDTO = ProductDTO.builder()
                .name("Updated Product Name")
                .price(BigDecimal.valueOf(20.0))
                .category(CategoryEnum.COFFEE)
                .build();

        Product existingProduct = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

        ProductDTO updatedProduct = productService.updateProduct(productId.toString(), productDTO);

        assertEquals(productDTO.getName(), updatedProduct.getName());
        assertEquals(productDTO.getPrice(), updatedProduct.getPrice());
        assertEquals(productDTO.getCategory(), updatedProduct.getCategory());
    }

    @Test
    void deleteProduct_ExistingProductId_DeletesProduct() {
        UUID productId = UUID.randomUUID();

        productService.deleteProduct(productId.toString());

        verify(productRepository, times(1)).deleteById(productId);
    }
}

package com.example.trafileatestproject.service;

import com.example.trafileatestproject.exceptions.EntityNotFoundException;
import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.CartProductDTO;
import com.example.trafileatestproject.model.api.CategoryEnum;
import com.example.trafileatestproject.model.api.ProductDTO;
import com.example.trafileatestproject.model.api.ProductQuantityDTO;
import com.example.trafileatestproject.model.api.TotalsDTO;
import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.model.entity.Cart;
import com.example.trafileatestproject.model.entity.CartProduct;
import com.example.trafileatestproject.model.entity.CartProductId;
import com.example.trafileatestproject.model.entity.Product;
import com.example.trafileatestproject.model.entity.User;
import com.example.trafileatestproject.model.mapper.ProductMapper;
import com.example.trafileatestproject.model.mapper.UserMapper;
import com.example.trafileatestproject.repository.CartProductRepository;
import com.example.trafileatestproject.repository.CartRepository;
import com.example.trafileatestproject.service.impl.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private IUserService userService;

    @Mock
    private IProductService productService;

    @Mock
    private CartProductRepository cartProductRepository;

    private CartServiceImpl cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(cartRepository, userService, productService, cartProductRepository);
    }

    @Test
    void createEmptyCart_ValidUserId_ReturnsCartDTO() {
        String userId = UUID.randomUUID().toString();
        UserDTO userDTO = UserDTO.builder()
                .id(UUID.fromString(userId))
                .name("John Doe")
                .build();

        Cart cart = Cart.builder()
                .id(UUID.randomUUID())
                .user(new UserMapper().toEntity(userDTO))
                .build();

        when(userService.getUserById(userId)).thenReturn(userDTO);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        CartDTO cartDTO = cartService.createEmptyCart(userId);

        assertNotNull(cartDTO);
        assertEquals(userDTO, cartDTO.getUser());
    }

    @Test
    void addProducts_ValidProductIdAndQuantity_ReturnsUpdatedCartDTO() {
        String cartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                .productId(productId)
                .quantity(2)
                .build();

        User user = User.builder()
                .name("Updated User Name")
                .age(20)
                .phoneNumber("351222222")
                .build();

        Cart cart = Cart.builder()
                .user(user)
                .id(UUID.fromString(cartId))
                .build();

        ProductDTO productDTO = ProductDTO.builder()
                .id(productId)
                .name("Test Product")
                .price(BigDecimal.valueOf(20))
                .category(CategoryEnum.ACCESSORIES)
                .build();

        Set<CartProduct> cartProductSet = new HashSet<>();
        cartProductSet.add(CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), productId))
                .cart(cart)
                .product(new ProductMapper().toEntity(productDTO))
                .quantity(2)
                .build());

        Set<ProductQuantityDTO> productQuantityDTOList = new HashSet<>();
        productQuantityDTOList.add(productQuantityDTO);

        when(cartRepository.findById(UUID.fromString(cartId))).thenReturn(Optional.of(cart));
        when(productService.getProductById(productId.toString())).thenReturn(productDTO);
        when(cartProductRepository.findAllByCartId(cart.getId())).thenReturn(new ArrayList<>());
        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(null);

        // Service call
        CartDTO updatedCartDTO = cartService.addProducts(cartId, productQuantityDTO);

        verify(cartProductRepository).findAllByCartId(cart.getId());
        assertNotNull(updatedCartDTO);
        assertEquals(productQuantityDTOList, updatedCartDTO.getProductQuantity());
    }

    @Test
    void addProducts_InvalidProductId_ThrowsEntityNotFoundException() {
        String cartId = UUID.randomUUID().toString();
        UUID invalidProductId = UUID.randomUUID();
        ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                .productId(invalidProductId)
                .quantity(2)
                .build();

        Cart cart = Cart.builder()
                .id(UUID.fromString(cartId))
                .build();

        when(cartRepository.findById(UUID.fromString(cartId))).thenReturn(Optional.of(cart));
        when(productService.getProductById(invalidProductId.toString())).thenThrow(EntityNotFoundException.class);

        assertThrows(EntityNotFoundException.class, () -> cartService.addProducts(cartId, productQuantityDTO));
    }

    @Test
    void addProducts_InvalidQuantity_ThrowsValidationException() {
        String cartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                .productId(productId)
                .quantity(-1)  // Invalid quantity
                .build();

        Cart cart = Cart.builder()
                .id(UUID.fromString(cartId))
                .build();

        when(cartRepository.findById(UUID.fromString(cartId))).thenReturn(Optional.of(cart));

        assertThrows(ValidationException.class, () -> cartService.addProducts(cartId, productQuantityDTO));
    }

    @Test
    void modifyProductQuantity_ValidInput_ReturnsUpdatedCartProductDTO() {
        String cartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        int newQuantity = 3;

        CartProduct cartProduct = CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), productId))
                .product(Product.builder().id(productId).name("Test Product").price(BigDecimal.valueOf(30)).category(CategoryEnum.ACCESSORIES).build())
                .quantity(2)
                .build();

        when(cartProductRepository.findByCartIdAndProductId(UUID.fromString(cartId), productId)).thenReturn(Optional.of(cartProduct));
        when(cartProductRepository.save(any(CartProduct.class))).thenReturn(cartProduct);

        // Call service
        CartProductDTO updatedCartProductDTO = cartService.modifyProductQuantity(cartId, productId.toString(), newQuantity);

        // Assert
        assertNotNull(updatedCartProductDTO);
        assertEquals(newQuantity, updatedCartProductDTO.getQuantity());
    }

    @Test
    void modifyProductQuantity_ProductNotFound_ThrowsEntityNotFoundException() {
        String cartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        int newQuantity = 3;

        when(cartProductRepository.findByCartIdAndProductId(UUID.fromString(cartId), productId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.modifyProductQuantity(cartId, productId.toString(), newQuantity));
    }

    @Test
    void getCartById_ValidCartId_ReturnsCartDTO() {
        String cartId = UUID.randomUUID().toString();
        Cart cart = Cart.builder()
                .id(UUID.fromString(cartId))
                .user(User.builder().id(UUID.randomUUID()).name("Test User").build())
                .build();

        CartProduct cartProduct = CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), UUID.randomUUID()))
                .product(Product.builder().id(UUID.randomUUID()).name("Test Product").price(BigDecimal.valueOf(30)).category(CategoryEnum.ACCESSORIES).build())
                .quantity(2)
                .build();

        List<CartProduct> cartProductSet = new ArrayList<>();
        cartProductSet.add(cartProduct);

        when(cartRepository.findById(UUID.fromString(cartId))).thenReturn(Optional.of(cart));
        when(cartProductRepository.findAllByCartId(cart.getId())).thenReturn(cartProductSet);

        // Act
        CartDTO cartDTO = cartService.getCartById(cartId);

        // Assert
        assertNotNull(cartDTO);
        assertEquals(cart.getId(), cartDTO.getId());
        assertNotNull(cartDTO.getUser());
        assertEquals(cart.getUser().getName(), cartDTO.getUser().getName());
        assertFalse(cartDTO.getProductQuantity().isEmpty());
        assertEquals(cartProduct.getQuantity(), cartDTO.getProductQuantity().iterator().next().getQuantity());
    }

    @Test
    void calculateTotals_ValidCartId_ReturnsTotalsDTO() {
        String cartId = UUID.randomUUID().toString();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();

        Product product1 = Product.builder()
                .id(productId1)
                .name("Product 1")
                .price(BigDecimal.valueOf(20))
                .category(CategoryEnum.ACCESSORIES)
                .build();

        Product product2 = Product.builder()
                .id(productId2)
                .name("Product 2")
                .price(BigDecimal.valueOf(30))
                .category(CategoryEnum.EQUIPMENT)
                .build();

        CartProduct cartProduct1 = CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), productId1))
                .product(product1)
                .quantity(2)
                .build();

        CartProduct cartProduct2 = CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), productId2))
                .product(product2)
                .quantity(1)
                .build();

        when(cartProductRepository.findAllByCartId(UUID.fromString(cartId))).thenReturn(List.of(cartProduct1, cartProduct2));

        //Call service
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        assertNotNull(totalsDTO);
        assertEquals(2, totalsDTO.getProducts());
        assertEquals(BigDecimal.valueOf(8.0), totalsDTO.getShippingPrice());
        //assertEquals(BigDecimal.valueOf(62.0), totalsDTO.getTotalPrice());
        //assertEquals(BigDecimal.valueOf(0.00), totalsDTO.getDiscounts());
    }

    @Test
    void addProducts_InvalidCartId_ThrowsEntityNotFoundException() {
        // Arrange
        String invalidCartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        int quantity = 2;

        ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        when(cartRepository.findById(UUID.fromString(invalidCartId))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> cartService.addProducts(invalidCartId, productQuantityDTO));
    }
}

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
        UUID productId3 = UUID.randomUUID();
        UUID productId4 = UUID.randomUUID();
        UUID productId5 = UUID.randomUUID();

        BigDecimal product1Price = BigDecimal.valueOf(10);
        BigDecimal product2Price = BigDecimal.valueOf(20);
        BigDecimal product3Price = BigDecimal.valueOf(30);
        BigDecimal product4Price = BigDecimal.valueOf(40);
        BigDecimal product5Price = BigDecimal.valueOf(50);

        //Create 3 products --> Category coffee
        Product product1 = createProduct(productId1, "Product 1", product1Price, CategoryEnum.ACCESSORIES);
        Product product2 = createProduct(productId2, "Product 2", product2Price, CategoryEnum.EQUIPMENT);
        Product product3 = createProduct(productId3, "Product 3", product3Price, CategoryEnum.COFFEE);
        Product product4 = createProduct(productId4, "Product 4", product4Price, CategoryEnum.COFFEE);
        Product product5 = createProduct(productId5, "Product 5", product5Price, CategoryEnum.COFFEE);

        /*
        Shipping 8 --> Base price + 1 (accessories) + 2 (equipment)
        Products --> 5. The quantity of each product can be variable
        Discounts --> 30. The cheapest coffee product should be free

        Total = 160 - 30 + 8 = 138
        */

        CartProduct cartProduct1 = createCartProduct(cartId, productId1, product1, 2);
        CartProduct cartProduct2 = createCartProduct(cartId, productId2, product2, 1);
        CartProduct cartProduct3 = createCartProduct(cartId, productId3, product3, 1);
        CartProduct cartProduct4 = createCartProduct(cartId, productId4, product4, 1);
        CartProduct cartProduct5 = createCartProduct(cartId, productId5, product5, 1);

        when(cartProductRepository.findAllByCartId(UUID.fromString(cartId)))
                .thenReturn(List.of(cartProduct1, cartProduct2, cartProduct3, cartProduct4, cartProduct5));

        //Call service
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        assertNotNull(totalsDTO);
        assertEquals(5, totalsDTO.getProducts());

        //To simplify these magic numbers are exposed, but should be calculated
        assertEquals(0, BigDecimal.valueOf(8).compareTo(totalsDTO.getShippingPrice()));
        assertEquals(0, BigDecimal.valueOf(30).compareTo(totalsDTO.getDiscounts()));
        assertEquals(0, BigDecimal.valueOf(138).compareTo(totalsDTO.getTotalPrice()));
    }

    @Test
    void calculateTotals_ValidCartIdWithCoffeePromotion_ReturnsFreeCoffeeProduct() {
        String cartId = UUID.randomUUID().toString();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();

        BigDecimal cheapestProductPrice = BigDecimal.valueOf(20);
        BigDecimal product2Price = BigDecimal.valueOf(30);
        BigDecimal product3Price = BigDecimal.valueOf(40);

        //Create 3 products --> Category coffee
        Product product1 = createProduct(productId1, "Product 1", cheapestProductPrice, CategoryEnum.COFFEE);
        Product product2 = createProduct(productId2, "Product 2", product2Price, CategoryEnum.COFFEE);
        Product product3 = createProduct(productId3, "Product 3", product3Price, CategoryEnum.COFFEE);

        BigDecimal expectedShippingPrice = BigDecimal.valueOf(5); //Base price
        int expectedProducts = 3;

        /*
        Shipping 5 --> Base price
        Products --> 3. The quantity of each product can be variable
        Discounts --> 20. The cheapest product should be free

        Total = 90 - 20 + 5 = 75
        */

        BigDecimal expectedTotalPrice =
                product2Price
                        .add(product3Price)
                        .add(expectedShippingPrice);

        CartProduct cartProduct1 = createCartProduct(cartId, productId1, product1, 1);
        CartProduct cartProduct2 = createCartProduct(cartId, productId2, product2, 1);
        CartProduct cartProduct3 = createCartProduct(cartId, productId3, product3, 1);

        when(cartProductRepository.findAllByCartId(UUID.fromString(cartId))).thenReturn(List.of(cartProduct1, cartProduct2, cartProduct3));

        //Call service
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        assertNotNull(totalsDTO);
        assertEquals(expectedProducts, totalsDTO.getProducts());
        assertEquals(0, expectedShippingPrice.compareTo(totalsDTO.getShippingPrice()));
        assertEquals(0, cheapestProductPrice.compareTo(totalsDTO.getDiscounts()));
        assertEquals(0, expectedTotalPrice.compareTo(totalsDTO.getTotalPrice()));
    }

    @Test
    void calculateTotals_ValidCartIdWithEquipmentPromotion_ReturnsFreeShipping() {
        String cartId = UUID.randomUUID().toString();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();

        BigDecimal product1Price = BigDecimal.valueOf(20);
        BigDecimal product2Price = BigDecimal.valueOf(30);
        BigDecimal product3Price = BigDecimal.valueOf(40);

        //Create 3 products --> Category equipment
        Product product1 = createProduct(productId1, "Product 1", product1Price, CategoryEnum.EQUIPMENT);
        Product product2 = createProduct(productId2, "Product 2", product2Price, CategoryEnum.EQUIPMENT);
        Product product3 = createProduct(productId3, "Product 3", product3Price, CategoryEnum.EQUIPMENT);

        /*
        Shipping 0 --> Free shipping
        Products --> 3. Three different equipment products (Quantities can vary)
        Discounts --> 0. The cheapest product should be free

        Total = 90 - 0 + 0 = 90
        */
        BigDecimal expectedTotalPrice = product1Price
                .add(product2Price)
                .add(product3Price);

        CartProduct cartProduct1 = createCartProduct(cartId, productId1, product1, 1);
        CartProduct cartProduct2 = createCartProduct(cartId, productId2, product2, 1);
        CartProduct cartProduct3 = createCartProduct(cartId, productId3, product3, 1);

        when(cartProductRepository.findAllByCartId(UUID.fromString(cartId))).thenReturn(List.of(cartProduct1, cartProduct2, cartProduct3));

        //Call service
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        assertNotNull(totalsDTO);
        assertEquals(3, totalsDTO.getProducts());
        assertEquals(0, BigDecimal.valueOf(0).compareTo(totalsDTO.getShippingPrice()));
        assertEquals(0, BigDecimal.valueOf(0).compareTo(totalsDTO.getDiscounts()));
        assertEquals(0, expectedTotalPrice.compareTo(totalsDTO.getTotalPrice()));
    }

    @Test
    void calculateTotals_ValidCartIdWithAccessoriesPromotion_AppliesDiscountInAccessories() {
        String cartId = UUID.randomUUID().toString();
        UUID productId1 = UUID.randomUUID();
        UUID productId2 = UUID.randomUUID();
        UUID productId3 = UUID.randomUUID();

        /*Create 3 products --> Category accessories
        The products sum a total of 90. So, the discount is applicable
                FIRST PRODUCT PRICE = 100 / 1.10 = 90
                SECOND PRODUCT PRICE = 200 / 1.10 = 180
                THIRD PRODUCT PRICE = 300 / 1.10 = 270*/

        BigDecimal product1Price = BigDecimal.valueOf(100);
        BigDecimal product2Price = BigDecimal.valueOf(200);
        BigDecimal product3Price = BigDecimal.valueOf(300);

        Product product1 = createProduct(productId1, "Product 1", product1Price, CategoryEnum.ACCESSORIES);
        Product product2 = createProduct(productId2, "Product 2", product2Price, CategoryEnum.ACCESSORIES);
        Product product3 = createProduct(productId3, "Product 3", product3Price, CategoryEnum.ACCESSORIES);

        CartProduct cartProduct1 = createCartProduct(cartId, productId1, product1, 1);
        CartProduct cartProduct2 = createCartProduct(cartId, productId2, product2, 1);
        CartProduct cartProduct3 = createCartProduct(cartId, productId3, product3, 1);

        /*
        Shipping 6 --> 5 base cost + 1 for buying accessories (fixed price)
        Products --> 3. Three different equipment products (Quantities can vary)
        Discounts --> 10 + 20 + 30 --> 10% of discount = 60

        Total = 600 - 60 + 6 = 546
        */
        BigDecimal expectedShippingPrice = BigDecimal.valueOf(5)
                .add(BigDecimal.valueOf(1)); //Base price + 1 (accessories)

        when(cartProductRepository.findAllByCartId(UUID.fromString(cartId))).thenReturn(List.of(cartProduct1, cartProduct2, cartProduct3));

        //Call service
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        assertNotNull(totalsDTO);
        assertEquals(3, totalsDTO.getProducts());
        assertEquals(0, expectedShippingPrice.compareTo(totalsDTO.getShippingPrice()));

        //To simplify these magic numbers are exposed, but should be calculated
        assertEquals(0, BigDecimal.valueOf(60).compareTo(totalsDTO.getDiscounts()));
        assertEquals(0, BigDecimal.valueOf(546).compareTo(totalsDTO.getTotalPrice()));
    }

    @Test
    void addProducts_InvalidCartId_ThrowsEntityNotFoundException() {
        String invalidCartId = UUID.randomUUID().toString();
        UUID productId = UUID.randomUUID();
        int quantity = 2;

        ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                .productId(productId)
                .quantity(quantity)
                .build();

        when(cartRepository.findById(UUID.fromString(invalidCartId))).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cartService.addProducts(invalidCartId, productQuantityDTO));
    }

    private Product createProduct(UUID id, String name, BigDecimal price, CategoryEnum category) {
        return Product.builder().id(id).name(name).price(price).category(category).build();
    }

    private CartProduct createCartProduct(String cartId, UUID productId, Product product, int quantity) {
        return CartProduct.builder()
                .id(new CartProductId(UUID.fromString(cartId), productId))
                .product(product)
                .quantity(quantity)
                .build();
    }
}

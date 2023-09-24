package com.example.trafileatestproject.service.impl;

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
import com.example.trafileatestproject.model.mapper.CartMapper;
import com.example.trafileatestproject.model.mapper.CartProductMapper;
import com.example.trafileatestproject.model.mapper.ProductMapper;
import com.example.trafileatestproject.model.mapper.UserMapper;
import com.example.trafileatestproject.repository.CartProductRepository;
import com.example.trafileatestproject.repository.CartRepository;
import com.example.trafileatestproject.service.ICartService;
import com.example.trafileatestproject.service.IProductService;
import com.example.trafileatestproject.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements ICartService {
    private static final BigDecimal BASE_SHIPPING_COST = BigDecimal.valueOf(5.0);
    private static final BigDecimal ADDITIONAL_PER_ACCESSORY = BigDecimal.valueOf(1.0);
    private static final BigDecimal ADDITIONAL_PER_EQUIPMENT = BigDecimal.valueOf(2.0);
    private static final BigDecimal ACCESSORIES_SPECIAL_OFFER_PRICE = BigDecimal.valueOf(70.0);
    private static final int ACCESSORIES_PERCENTAGE_DISCOUNT = 10;

    private final CartRepository cartRepository;
    private final IUserService userService;
    private final IProductService productService;
    private final CartProductRepository cartProductRepository;

    @Override
    @Transactional
    public CartDTO createEmptyCart(String userId) {
        UserDTO userDTO = userService.getUserById(userId);

        Cart createdCart = Cart.builder()
                .id(UUID.randomUUID())
                .user(new UserMapper().toEntity(userDTO))
                .build();

        Cart cart = cartRepository.save(createdCart);

        return new CartMapper().toDto(cart);
    }

    @Override
    @Transactional
    public CartDTO addProducts(String id, ProductQuantityDTO productQuantityDTO) {
        validateRequest(productQuantityDTO);
        Set<ProductQuantityDTO> productQuantityDTOList = new HashSet<>();

        // First get the cart created without products
        Cart cart = cartRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Cart not found", "Could not find cart.", id));

        // Then check if the product exists
        ProductDTO productDTO = productService.getProductById(productQuantityDTO.getProductId().toString());

        Map<UUID, CartProduct> cartProductMap = new HashMap<>();

        // Retrieve existing CartProducts and populate the map
        List<CartProduct> existingCartProducts = cartProductRepository.findAllByCartId(cart.getId());
        for (CartProduct cp : existingCartProducts) {
            cartProductMap.put(cp.getProduct().getId(), cp);
        }

        // Update or add the new CartProduct
        CartProduct cartProduct = cartProductMap.get(productQuantityDTO.getProductId());
        if (cartProduct != null) {
            // Update the quantity if the CartProduct already exists
            cartProduct.setQuantity(productQuantityDTO.getQuantity());
        } else {
            // Create a new CartProduct if it doesn't exist
            CartProductId cartProductId = CartProductId.builder()
                    .cartId(cart.getId())
                    .productId(productQuantityDTO.getProductId())
                    .build();

            cartProduct = CartProduct.builder()
                    .id(cartProductId)
                    .product(new ProductMapper().toEntity(productDTO))
                    .cart(cart)
                    .quantity(productQuantityDTO.getQuantity())
                    .build();
        }

        // Save the updated or new CartProduct
        cartProductRepository.save(cartProduct);

        // Add the product you just added to the productQuantityDTOList
        productQuantityDTOList.add(ProductQuantityDTO.builder()
                .productId(productQuantityDTO.getProductId())
                .quantity(productQuantityDTO.getQuantity())
                .build());

        // Build the response DTO
        for (CartProduct cp : cartProductMap.values()) {
            productQuantityDTOList.add(ProductQuantityDTO.builder()
                    .productId(cp.getProduct().getId())
                    .quantity(cp.getQuantity())
                    .build());
        }

        return CartDTO.builder()
                .id(cart.getId())
                .productQuantity(productQuantityDTOList)
                .user(new UserMapper().toDto(cart.getUser()))
                .build();
    }

    @Override
    @Transactional
    public CartProductDTO modifyProductQuantity(String id, String productId, int quantity) {
        //Check first if the product exists
        CartProduct cartProduct = cartProductRepository.findByCartIdAndProductId(UUID.fromString(id), UUID.fromString(productId))
                .orElseThrow(() ->
                        new EntityNotFoundException("Cart not found", "Could not find cart for this productId.", id, productId, quantity));

        //Set the new quantity
        cartProduct.setQuantity(quantity);

        //Update the cart Product
        cartProductRepository.save(cartProduct);

        return new CartProductMapper().toDto(cartProduct);
    }

    @Override
    public CartDTO getCartById(String id) {
        Cart cart = cartRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new EntityNotFoundException("Cart not found", "Could not find cart.", id));

        Set<CartProduct> cartProductDTO = new HashSet<>(cartProductRepository.findAllByCartId(cart.getId()));
        Set<ProductQuantityDTO> productQuantityDTOSet = new HashSet<>();

        for (CartProduct cartProduct : cartProductDTO) {
            ProductQuantityDTO productQuantityDTO = ProductQuantityDTO.builder()
                    .productId(cartProduct.getProduct().getId())
                    .quantity(cartProduct.getQuantity())
                    .build();

            productQuantityDTOSet.add(productQuantityDTO);
        }

        CartDTO cartDTO = new CartMapper().toDto(cart);
        cartDTO.setProductQuantity(productQuantityDTOSet);

        return cartDTO;
    }

    @Override
    public TotalsDTO calculateTotals(String cartId) {
        List<CartProduct> cartProducts = cartProductRepository.findAllByCartId(UUID.fromString(cartId));

        int countProducts = countProducts(cartProducts);
        BigDecimal shippingPrice = calculateShippingPrice(cartProducts);
        BigDecimal discounts = calculateDiscounts(cartProducts);
        BigDecimal totalPrice = calculateCartTotalPrice(cartProducts, shippingPrice);

        return TotalsDTO.builder()
                .shippingPrice(shippingPrice)
                .totalPrice(totalPrice)
                .discounts(discounts)
                .products(countProducts)
                .build();
    }

    private BigDecimal calculateCartTotalPrice(List<CartProduct> cartProducts, BigDecimal shippingPrice) {
        /*Important... In the case of the coffee discount... I'm going to apply a discount in the cheapest product.
        I have thought about a voucher system but to simplify I think is better this approach*/

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (CartProduct cp : cartProducts) {
            BigDecimal productPrice = cp.getProduct().getPrice().multiply(BigDecimal.valueOf(cp.getQuantity()));

            if (cp.getProduct().getCategory() == CategoryEnum.ACCESSORIES) {
                if (isAccessoriesSpecialOfferApplicable(cartProducts)) {
                    // Apply 10% discount to accessories
                    BigDecimal discountedPrice = productPrice.multiply(BigDecimal.valueOf(1.0 - ACCESSORIES_PERCENTAGE_DISCOUNT / 100.0));
                    totalPrice = totalPrice.add(discountedPrice);
                } else {
                    // If the special offer is not applicable, use the original price
                    totalPrice = totalPrice.add(productPrice);
                }
            } else {
                // For other products, add the original price
                totalPrice = totalPrice.add(productPrice);
            }
        }

        CartProduct coffeeCheapestProduct;
        BigDecimal coffeeCheapestProductPrice = BigDecimal.valueOf(0);

        if (isCoffeeSpecialOfferApplicable(cartProducts)) {
            coffeeCheapestProduct = getCheapestCoffeeProduct(cartProducts);
            coffeeCheapestProductPrice = coffeeCheapestProduct.getProduct().getPrice();
        }

        // Subtract shipping cost
        totalPrice = totalPrice.add(shippingPrice).subtract(coffeeCheapestProductPrice);

        return totalPrice.setScale(2, RoundingMode.UP);
    }

    private int countProducts(List<CartProduct> cartProducts) {
        return cartProducts.size();
    }

    private BigDecimal calculateShippingPrice(List<CartProduct> cartProducts) {
        int equipmentCount = 0;
        int accessoryCount = 0;

        // Count the number of equipment and accessory products
        for (CartProduct cartProduct : cartProducts) {
            Product product = cartProduct.getProduct();

            if (product.getCategory() == CategoryEnum.EQUIPMENT) {
                equipmentCount += cartProduct.getQuantity();
            } else if (product.getCategory() == CategoryEnum.ACCESSORIES) {
                accessoryCount += cartProduct.getQuantity();
            }
        }

        // Calculate shipping cost
        BigDecimal shippingCost = BASE_SHIPPING_COST;

        if (equipmentCount >= 3) {
            shippingCost = BigDecimal.valueOf(0);
        } else {
            if (accessoryCount > 0) {
                shippingCost = shippingCost.add(ADDITIONAL_PER_ACCESSORY);
            }
            if (equipmentCount > 0) {
                shippingCost = shippingCost.add(ADDITIONAL_PER_EQUIPMENT);
            }
        }

        return shippingCost;
    }

    private Boolean isAccessoriesSpecialOfferApplicable(List<CartProduct> cartProducts) {
        BigDecimal accessoriesTotalPrice = BigDecimal.valueOf(0);

        for (CartProduct cartProduct : cartProducts) {
            Product product = cartProduct.getProduct();
            if (product.getCategory() == CategoryEnum.ACCESSORIES) {
                accessoriesTotalPrice = accessoriesTotalPrice.add
                        (product.getPrice().multiply(BigDecimal.valueOf(cartProduct.getQuantity())));
            }
        }

        return accessoriesTotalPrice.compareTo(ACCESSORIES_SPECIAL_OFFER_PRICE) > 0;
    }

    private Boolean isCoffeeSpecialOfferApplicable(List<CartProduct> cartProducts) {
        int coffeeCount = 0;

        for (CartProduct cartProduct : cartProducts) {
            Product product = cartProduct.getProduct();
            if (product.getCategory() == CategoryEnum.COFFEE) {
                coffeeCount += 1;
            }
            if (coffeeCount == 2) {
                return true;
            }
        }

        return false;
    }

    private CartProduct getCheapestCoffeeProduct(List<CartProduct> cartProducts) {
        return cartProducts.stream()
                .filter(cp -> cp.getProduct().getCategory() == CategoryEnum.COFFEE)
                .min(Comparator.comparing(cp -> cp.getProduct().getPrice())).orElse(null);
    }

    private BigDecimal calculateDiscounts(List<CartProduct> cartProducts) {
        BigDecimal totalDiscounts = BigDecimal.ZERO;

        for (CartProduct cp : cartProducts) {
            BigDecimal productPrice = cp.getProduct().getPrice().multiply(BigDecimal.valueOf(cp.getQuantity()));

            if (cp.getProduct().getCategory() == CategoryEnum.ACCESSORIES && isAccessoriesSpecialOfferApplicable(cartProducts)) {
                BigDecimal discountedPrice = productPrice.multiply(BigDecimal.valueOf(1.0 - ACCESSORIES_PERCENTAGE_DISCOUNT / 100.0));
                totalDiscounts = totalDiscounts.add(productPrice.subtract(discountedPrice));
            }
        }

        if (isCoffeeSpecialOfferApplicable(cartProducts)) {
            CartProduct coffeeCheapestProduct = getCheapestCoffeeProduct(cartProducts);
            BigDecimal coffeeCheapestProductPrice = coffeeCheapestProduct.getProduct().getPrice();
            totalDiscounts = totalDiscounts.add(coffeeCheapestProductPrice);
        }

        return totalDiscounts.setScale(2, RoundingMode.UP);
    }

    private void validateRequest(ProductQuantityDTO productQuantityDTO) {
        if (productQuantityDTO.getProductId() == null || productQuantityDTO.getQuantity() <= 0) {
            throw new ValidationException(
                    "MissingRequiredParameters",
                    "ProductId and quantity are required",
                    productQuantityDTO.getProductId(),
                    productQuantityDTO.getQuantity()
            );
        }
    }
}

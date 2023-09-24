package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.OrderDTO;
import com.example.trafileatestproject.model.api.TotalsDTO;
import com.example.trafileatestproject.model.api.UserDTO;
import com.example.trafileatestproject.repository.OrderRepository;
import com.example.trafileatestproject.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class OrderServiceImplTest {

    @Mock
    private ICartService cartService;

    @Mock
    private OrderRepository orderRepository;

    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        orderService = new OrderServiceImpl(cartService, orderRepository);
    }

    @Test
    void createOrder_ValidCartId_ReturnsOrderDTO() {
        String cartId = UUID.randomUUID().toString();

        UserDTO userDTO = UserDTO.builder()
                .name("Updated User Name")
                .age(20)
                .phoneNumber("351222222")
                .build();

        CartDTO cartDTO = CartDTO.builder()
                .user(userDTO)
                .id(UUID.fromString(cartId))
                .build();

        TotalsDTO totalsDTO = TotalsDTO.builder()
                .products(3)
                .discounts(BigDecimal.valueOf(10.0))
                .shippingPrice(BigDecimal.valueOf(5.0))
                .totalPrice(BigDecimal.valueOf(50.0))
                .build();

        when(cartService.getCartById(cartId)).thenReturn(cartDTO);
        when(cartService.calculateTotals(cartId)).thenReturn(totalsDTO);

        OrderDTO orderDTO = orderService.createOrder(cartId);

        assertEquals(cartDTO, orderDTO.getCart());
        assertEquals(totalsDTO, orderDTO.getTotals());
    }
}

package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.exceptions.ValidationException;
import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.OrderDTO;
import com.example.trafileatestproject.model.api.TotalsDTO;
import com.example.trafileatestproject.model.entity.Order;
import com.example.trafileatestproject.model.mapper.OrderMapper;
import com.example.trafileatestproject.repository.OrderRepository;
import com.example.trafileatestproject.service.ICartService;
import com.example.trafileatestproject.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final ICartService cartService;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public OrderDTO createOrder(String cartId) {
        CartDTO cartDTO = cartService.getCartById(cartId);

        if (cartDTO.getProductQuantity() == null || cartDTO.getProductQuantity().isEmpty()) {
            throw new ValidationException(
                    "Empty Cart",
                    "Cart must contain products to create an order",
                    cartId);
        }

        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        OrderDTO orderDTO = OrderDTO.builder()
                .cart(cartDTO)
                .totals(totalsDTO)
                .build();

        Order order = orderRepository.save(new OrderMapper().toEntity(orderDTO));
        orderDTO.setId(order.getId());

        return orderDTO;
    }
}

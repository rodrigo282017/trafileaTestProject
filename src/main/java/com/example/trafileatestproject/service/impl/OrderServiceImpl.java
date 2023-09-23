package com.example.trafileatestproject.service.impl;

import com.example.trafileatestproject.model.api.CartDTO;
import com.example.trafileatestproject.model.api.OrderDTO;
import com.example.trafileatestproject.model.api.TotalsDTO;
import com.example.trafileatestproject.model.mapper.OrderMapper;
import com.example.trafileatestproject.repository.OrderRepository;
import com.example.trafileatestproject.service.ICartService;
import com.example.trafileatestproject.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {
    private final ICartService cartService;
    private final OrderRepository orderRepository;

    @Override
    public OrderDTO createOrder(String cartId) {
        CartDTO cartDTO = cartService.getCartById(cartId);
        TotalsDTO totalsDTO = cartService.calculateTotals(cartId);

        OrderDTO orderDTO = OrderDTO.builder()
                .cart(cartDTO)
                .totals(totalsDTO)
                .build();

        orderRepository.save(new OrderMapper().toEntity(orderDTO));

        return orderDTO;
    }
}

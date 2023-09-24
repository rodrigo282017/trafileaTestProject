package com.example.trafileatestproject.model.mapper;

import com.example.trafileatestproject.model.api.OrderDTO;
import com.example.trafileatestproject.model.api.TotalsDTO;
import com.example.trafileatestproject.model.entity.Order;
import com.example.trafileatestproject.model.entity.Totals;
import com.example.trafileatestproject.util.Mapper;

import java.util.List;

public class OrderMapper implements Mapper<OrderDTO, Order> {

    @Override
    public OrderDTO toDto(Order order) {
        return null;
    }

    @Override
    public List<OrderDTO> toDTOs(List<Order> orders) {
        return null;
    }

    @Override
    public Order toEntity(OrderDTO orderDTO) {
        TotalsDTO totalsDTO = orderDTO.getTotals();

        Totals totals = Totals.builder()
                .orderTotal(totalsDTO.getTotalPrice())
                .discounts(totalsDTO.getDiscounts())
                .products(totalsDTO.getProducts())
                .shipping(totalsDTO.getShippingPrice())
                .build();

        return Order.builder()
                .totals(totals)
                .cart(new CartMapper().toEntity(orderDTO.getCart()))
                .build();
    }
}

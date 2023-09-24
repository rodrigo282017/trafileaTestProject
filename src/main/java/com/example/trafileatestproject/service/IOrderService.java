package com.example.trafileatestproject.service;

import com.example.trafileatestproject.model.api.OrderDTO;

public interface IOrderService {
    OrderDTO createOrder(String cartId);
}

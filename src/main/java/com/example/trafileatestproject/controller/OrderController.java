package com.example.trafileatestproject.controller;

import com.example.trafileatestproject.model.api.CreateOrderDTO;
import com.example.trafileatestproject.model.api.OrderDTO;
import com.example.trafileatestproject.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final IOrderService orderService;
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        OrderDTO createdOrder = orderService.createOrder(createOrderDTO.getCartId());

        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }
}

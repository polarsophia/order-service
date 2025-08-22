package org.books.orderService.controller;

import org.books.orderService.domain.Order;
import org.books.orderService.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.books.orderService.service.OrderService;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @PostMapping
    public Mono<Order> submitOrder(
            @RequestBody @Valid OrderRequest orderRequest
            ) {
        return orderService.submitOrder(
                orderRequest.isbn(),
                orderRequest.quantity()
        );
    }
}

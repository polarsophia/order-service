package org.books.orderService.controller;

import org.books.orderService.domain.Order;
import org.books.orderService.dto.OrderRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.books.orderService.service.OrderService;

import java.security.Principal;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Flux<Order> getAllOrders(
            @AuthenticationPrincipal Jwt jwt
            ) {
        return orderService.getAllOrders(jwt.getSubject());
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

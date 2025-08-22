package org.books.orderService.service;

import org.books.orderService.domain.Order;
import org.books.orderService.domain.OrderStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.books.orderService.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final BookClient bookClient;

    public OrderService(OrderRepository orderRepository, BookClient bookClient) {
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> submitOrder(String bookIsbn, int quantity){
        return bookClient.getBookByIsbn(bookIsbn)
                .map((book) -> Order.of(
                        book.isbn(),
                        book.title() + " - " + book.author(),
                        book.price(),
                        quantity,
                        OrderStatus.ACCEPTED
                ))
                .defaultIfEmpty(Order.of(
                        bookIsbn,
                        null,
                        null,
                        quantity,
                        OrderStatus.REJECTED
                ))
                .flatMap(orderRepository::save);
    }
}

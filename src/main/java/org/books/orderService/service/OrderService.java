package org.books.orderService.service;

import org.books.orderService.domain.Order;
import org.books.orderService.domain.OrderStatus;
import org.books.orderService.dto.OrderAcceptedMessage;
import org.books.orderService.dto.OrderDispatchedMessage;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.books.orderService.repository.OrderRepository;

import java.util.logging.Logger;

@Service
public class OrderService {
    private final Logger logger = Logger.getLogger(OrderService.class.getName());
    private final OrderRepository orderRepository;
    private final BookClient bookClient;
    private final StreamBridge streamBridge;

    public OrderService(OrderRepository orderRepository, BookClient bookClient, StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
        this.orderRepository = orderRepository;
        this.bookClient = bookClient;
    }

    public Flux<Order> getAllOrders(String userId) {
        return orderRepository.findAllByCreatedBy(userId);
    }

    @Transactional
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
                .flatMap(orderRepository::save)
                .doOnNext(this::publishOrderAcceptedEvent);

    }

    public Flux<Order> consumeOrderDispatchEvent(Flux<OrderDispatchedMessage> flux){
        return flux
                .flatMap(message -> orderRepository.findById(message.orderId()))
                .map(order -> new Order(
                        order.id(),
                        order.bookIsbn(),
                        order.bookName(),
                        order.bookPrice(),
                        order.quantity(),
                        OrderStatus.DISPATCHED,
                        order.createdAt(),
                        order.updatedAt(),
                        order.createdBy(),
                        order.updatedBy(),
                        order.version()
                ))
                .flatMap(orderRepository::save);
    }

    public void publishOrderAcceptedEvent(Order order) {
        if(!order.status().equals(OrderStatus.ACCEPTED)) return;
        var orderAcceptedMessage = new OrderAcceptedMessage(order.id());
        logger.info("Sending order accepted event with id: " + order.id());
        var result = streamBridge.send("acceptOrder-out-0", orderAcceptedMessage);
        logger.info("Result of sending data for order with id " + order.id() + " : " + result);
    }
}

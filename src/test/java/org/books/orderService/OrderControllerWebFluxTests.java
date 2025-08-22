package org.books.orderService;

import org.books.orderService.controller.OrderController;
import org.books.orderService.domain.Order;
import org.books.orderService.domain.OrderStatus;
import org.books.orderService.dto.OrderRequest;
import org.books.orderService.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@WebFluxTest(OrderController.class)
public class OrderControllerWebFluxTests {
    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @Test
    void whenBookNotAvailableThenRejectOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = Order.of(
                orderRequest.isbn(),
                null,
                null,
                orderRequest.quantity(),
                OrderStatus.REJECTED
        );
        given(orderService.submitOrder(
                orderRequest.isbn(), orderRequest.quantity())
        ).willReturn(Mono.just(expectedOrder));
        webTestClient
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.REJECTED);
                });
    }

    @Test
    void whenBookAvailableThenAcceptOrder() {
        var orderRequest = new OrderRequest("1234567890", 3);
        var expectedOrder = Order.of(
                orderRequest.isbn(),
                "Test Book - Test Author",
                29.99,
                orderRequest.quantity(),
                OrderStatus.ACCEPTED
        );
        given(orderService.submitOrder(
                orderRequest.isbn(), orderRequest.quantity())
        ).willReturn(Mono.just(expectedOrder));
        webTestClient
                .post()
                .uri("/orders")
                .bodyValue(orderRequest)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(Order.class).value(actualOrder -> {
                    assertThat(actualOrder).isNotNull();
                    assertThat(actualOrder.status()).isEqualTo(OrderStatus.ACCEPTED);
                    assertThat(actualOrder.bookIsbn()).isEqualTo(expectedOrder.bookIsbn());
                    assertThat(actualOrder.quantity()).isEqualTo(expectedOrder.quantity());
                });
    }
}

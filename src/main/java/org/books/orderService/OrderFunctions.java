package org.books.orderService;

import org.books.orderService.domain.Order;
import org.books.orderService.domain.OrderStatus;
import org.books.orderService.dto.OrderAcceptedMessage;
import org.books.orderService.dto.OrderDispatchedMessage;
import org.books.orderService.service.OrderService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Logger;

@Configuration
public class OrderFunctions {
    private static final Logger logger = Logger.getLogger(OrderFunctions.class.getName());

    @Bean
    public Consumer<Flux<OrderDispatchedMessage>> dispatchOrder(
            OrderService orderService
    ){
        return flux ->
                orderService.consumeOrderDispatchEvent(flux)
                        .doOnNext(order ->
                                logger.info("The order with id " + order.id() + " has been dispatched.")
                        )
                        .subscribe();
    }
}

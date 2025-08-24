package org.books.orderService.domain;

import org.springframework.data.annotation.*;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Table("orders")
public record Order(
        @Id
        Long id,
        String bookIsbn,
        String bookName,
        Double bookPrice,
        Integer quantity,
        OrderStatus status,

        @CreatedDate
        Instant createdAt,

        @LastModifiedDate
        Instant updatedAt,

        @CreatedBy
        String createdBy,

        @LastModifiedBy
        String updatedBy,

        @Version
        int version
) {
    public static Order of(
            String bookIsbn,
            String bookName,
            Double bookPrice,
            Integer quantity,
            OrderStatus status
    ) {
        return new Order(null, bookIsbn, bookName, bookPrice, quantity, status, null, null, null, null, 0);
    }
}

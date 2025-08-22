package org.books.orderService.dto;

public record Book(
        String isbn,
        String title,
        String author,
        Double price
) {
}

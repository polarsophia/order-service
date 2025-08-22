package org.books.orderService.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequest(
        @NotNull(message = "Book ISBN must be defined")
        String isbn,
        @NotNull(message = "Quantity must be defined")
        @Min(value=1, message = "Quantity must be at least 1")
        @Max(value=5, message = "Quantity must not exceed 5")
        Integer quantity
) {
}

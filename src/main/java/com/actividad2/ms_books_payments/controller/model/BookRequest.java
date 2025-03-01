package com.actividad2.ms_books_payments.controller.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotNull(message = "`bookId` cannot be null")
    private String bookId;

    @NotNull(message = "`quantity` cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    private double price;
}

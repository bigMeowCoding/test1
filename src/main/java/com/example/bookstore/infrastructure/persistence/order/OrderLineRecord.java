package com.example.bookstore.infrastructure.persistence.order;

import java.math.BigDecimal;

public record OrderLineRecord(Long id, long orderId, long bookId, String bookTitle, BigDecimal unitPrice, int quantity,
                              BigDecimal lineAmount) { }

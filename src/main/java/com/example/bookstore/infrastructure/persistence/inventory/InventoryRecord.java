package com.example.bookstore.infrastructure.persistence.inventory;

public record InventoryRecord(long bookId, int availableQuantity, int reservedQuantity, int soldQuantity, long version) { }

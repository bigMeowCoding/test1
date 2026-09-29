package com.example.bookstore.infrastructure.persistence.inventory;

import com.example.bookstore.application.port.InventoryStore;
import com.example.bookstore.repository.InventoryMapper;
import org.springframework.stereotype.Component;

@Component
public final class InventoryPersistenceAdapter implements InventoryStore {
    private final InventoryMapper mapper;

    public InventoryPersistenceAdapter(InventoryMapper mapper) { this.mapper = mapper; }
    @Override public boolean reserve(long bookId, int quantity) { return mapper.reserve(bookId, quantity) == 1; }
    @Override public boolean confirmSale(long bookId, int quantity) { return mapper.confirmSale(bookId, quantity) == 1; }
    @Override public boolean release(long bookId, int quantity) { return mapper.release(bookId, quantity) == 1; }
}

package com.example.bookstore.domain.inventory;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InventoryTest {
    @Test
    void reserveThenReleaseRestoresAvailableStock() {
        Inventory inventory = new Inventory(1, 3, 0, 0, 0);
        inventory.reserve(2);
        inventory.release(2);
        assertEquals(3, inventory.availableQuantity());
        assertEquals(0, inventory.reservedQuantity());
    }

    @Test
    void cannotReserveMoreThanAvailableStock() {
        assertThrows(IllegalStateException.class, () -> new Inventory(1, 1, 0, 0, 0).reserve(2));
    }
}

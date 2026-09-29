package com.example.bookstore.util;

import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DatabaseInitializerSqlTest {
    @Test
    void initializationBackfillsOnlyUntouchedEmptyInventoryFromLegacyBookStock() throws Exception {
        try (InputStream input = getClass().getResourceAsStream("/db/init.sql")) {
            String sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);

            assertTrue(sql.contains("UPDATE inventories i\nJOIN books b ON b.id = i.book_id"));
            assertTrue(sql.contains("i.available_quantity = 0"));
            assertTrue(sql.contains("i.reserved_quantity = 0"));
            assertTrue(sql.contains("i.sold_quantity = 0"));
            assertTrue(sql.contains("b.stock > 0"));
        }
    }
}

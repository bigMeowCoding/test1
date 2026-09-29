package com.example.bookstore.application.port;

/** 库存端口：条件更新把“检查后扣减”合并为数据库原子操作。 */
public interface InventoryStore {
    boolean reserve(long bookId, int quantity);
    boolean confirmSale(long bookId, int quantity);
    boolean release(long bookId, int quantity);
}

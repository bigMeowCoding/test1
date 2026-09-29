package com.example.bookstore.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface InventoryMapper {
    int create(@Param("bookId") long bookId, @Param("availableQuantity") int availableQuantity);
    int updateAvailable(@Param("bookId") long bookId, @Param("availableQuantity") int availableQuantity);
    int reserve(@Param("bookId") long bookId, @Param("quantity") int quantity);
    int confirmSale(@Param("bookId") long bookId, @Param("quantity") int quantity);
    int release(@Param("bookId") long bookId, @Param("quantity") int quantity);
}

package com.example.bookstore.model;

import java.math.BigDecimal;

/**
 * 实体类：一行 books 表记录在 Java 中的表示。
 *
 * 初学阶段先使用普通 Java 类，便于观察 JDBC 如何把 ResultSet 转换成对象。
 */
public final class Book {
    private final Long id;
    private final String title;
    private final String author;
    private final BigDecimal price;
    private final int stock;

    public Book(Long id, String title, String author, BigDecimal price, int stock) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.stock = stock;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
}

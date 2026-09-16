package com.example.bookstore.service;

import com.example.bookstore.model.Book;

import java.util.List;

/** 业务层返回的分页结果，Web 层不需要知道 SQL 的 offset/limit。 */
public record BookPage(List<Book> books, int page, int totalPages, long total) { }

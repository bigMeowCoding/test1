package com.example.bookstore.service;

/** 指定业务资源不存在。 */
public final class NotFoundException extends RuntimeException {
    public NotFoundException(String message) { super(message); }
}

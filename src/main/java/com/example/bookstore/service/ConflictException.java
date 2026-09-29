package com.example.bookstore.service;

/** 当前业务状态不能接受请求，例如库存不足或非法状态迁移。 */
public final class ConflictException extends RuntimeException {
    public ConflictException(String message) { super(message); }
}

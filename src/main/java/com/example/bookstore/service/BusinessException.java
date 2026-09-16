package com.example.bookstore.service;

/** 可预期的业务失败，例如用户输入不合法或记录不存在。 */
public final class BusinessException extends RuntimeException {
    public BusinessException(String message) { super(message); }
    public BusinessException(String message, Throwable cause) { super(message, cause); }
}

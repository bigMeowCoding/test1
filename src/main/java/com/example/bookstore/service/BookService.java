package com.example.bookstore.service;

import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;

import java.math.BigDecimal;
import java.sql.SQLException;

/** 业务层：封装校验、分页规则和“记录必须存在”等业务含义。 */
public final class BookService {
    public static final int PAGE_SIZE = 5;
    private final BookRepository repository;

    public BookService(BookRepository repository) { this.repository = repository; }

    public BookPage list(String keyword, int requestedPage) {
        String normalizedKeyword = keyword == null ? "" : keyword.trim();
        int page = Math.max(1, requestedPage);
        try {
            long total = repository.countByKeyword(normalizedKeyword);
            int totalPages = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            page = Math.min(page, totalPages);
            return new BookPage(repository.findByKeyword(normalizedKeyword, (page - 1) * PAGE_SIZE, PAGE_SIZE),
                    page, totalPages, total);
        } catch (SQLException exception) {
            throw new BusinessException("查询书籍失败", exception);
        }
    }

    public Book get(long id) {
        try {
            return repository.findById(id).orElseThrow(() -> new BusinessException("书籍不存在或已被删除"));
        } catch (SQLException exception) {
            throw new BusinessException("查询书籍失败", exception);
        }
    }

    public void create(String title, String author, String price, String stock) {
        save(null, title, author, price, stock);
    }

    public void update(long id, String title, String author, String price, String stock) {
        Book book = buildBook(id, title, author, price, stock);
        try {
            if (!repository.update(book)) throw new BusinessException("书籍不存在或已被删除");
        } catch (SQLException exception) {
            throw new BusinessException("更新书籍失败", exception);
        }
    }

    public void delete(long id) {
        try {
            if (!repository.deleteById(id)) throw new BusinessException("书籍不存在或已被删除");
        } catch (SQLException exception) {
            throw new BusinessException("删除书籍失败", exception);
        }
    }

    private void save(Long id, String title, String author, String price, String stock) {
        try {
            repository.save(buildBook(id, title, author, price, stock));
        } catch (SQLException exception) {
            throw new BusinessException("保存书籍失败", exception);
        }
    }

    private Book buildBook(Long id, String title, String author, String priceText, String stockText) {
        String validTitle = required(title, "书名", 100);
        String validAuthor = required(author, "作者", 60);
        try {
            BigDecimal price = new BigDecimal(required(priceText, "价格", 20));
            int stock = Integer.parseInt(required(stockText, "库存", 10));
            if (price.signum() < 0) throw new BusinessException("价格不能小于 0");
            if (stock < 0) throw new BusinessException("库存不能小于 0");
            return new Book(id, validTitle, validAuthor, price, stock);
        } catch (NumberFormatException exception) {
            throw new BusinessException("价格必须是数字，库存必须是整数");
        }
    }

    private String required(String value, String fieldName, int maxLength) {
        if (value == null || value.isBlank()) throw new BusinessException(fieldName + "不能为空");
        String trimmed = value.trim();
        if (trimmed.length() > maxLength) throw new BusinessException(fieldName + "不能超过 " + maxLength + " 个字符");
        return trimmed;
    }
}

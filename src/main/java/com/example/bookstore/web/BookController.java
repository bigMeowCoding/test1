package com.example.bookstore.web;

import com.example.bookstore.service.BookPage;
import com.example.bookstore.service.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * REST API 的 Spring MVC 入口。
 *
 * <p>{@code @RestController} 会将返回值自动序列化为 JSON；各个 Mapping 注解替代了
 * Servlet 中按 HTTP 方法和路径分支的代码。业务规则仍在 {@link BookService}。</p>
 */
@RestController
@RequestMapping("/api/books")
public final class BookController {
    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String page) {
        BookPage result = bookService.list(keyword, positiveInt(page));
        return ResponseEntity.ok(Map.of("items", result.books(), "page", result.page(),
                "totalPages", result.totalPages(), "total", result.total()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable String id) {
        return ResponseEntity.ok(bookService.get(idFromPath(id)));
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody BookRequest request) {
        bookService.create(request.title(), request.author(), request.price().toPlainString(), request.stock().toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "书籍已创建"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @Valid @RequestBody BookRequest request) {
        bookService.update(idFromPath(id), request.title(), request.author(), request.price().toPlainString(), request.stock().toString());
        return ResponseEntity.ok(Map.of("message", "书籍已更新"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        bookService.delete(idFromPath(id));
        return ResponseEntity.noContent().build();
    }

    private long idFromPath(String idText) {
        try {
            long id = Long.parseLong(idText);
            if (id <= 0) {
                throw new NumberFormatException();
            }
            return id;
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("URL 中的书籍 ID 无效");
        }
    }

    private int positiveInt(String text) {
        try {
            return text == null ? 1 : Math.max(1, Integer.parseInt(text));
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

}

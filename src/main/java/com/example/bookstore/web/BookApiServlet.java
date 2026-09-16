package com.example.bookstore.web;

import com.example.bookstore.model.Book;
import com.example.bookstore.service.BookPage;
import com.example.bookstore.service.BookService;
import com.example.bookstore.service.BusinessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Map;

/** REST 风格接口：只接收/返回 JSON，不再生成 HTML。 */
public final class BookApiServlet extends HttpServlet {
    private static final ObjectMapper JSON = new ObjectMapper();
    private final BookService bookService;

    public BookApiServlet(BookService bookService) { this.bookService = bookService; }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String path = request.getPathInfo();
            if (path == null || path.equals("/")) {
                String keyword = request.getParameter("keyword");
                BookPage page = bookService.list(keyword, positiveInt(request.getParameter("page")));
                writeJson(response, HttpServletResponse.SC_OK, Map.of("items", page.books(), "page", page.page(),
                        "totalPages", page.totalPages(), "total", page.total()));
            } else {
                writeJson(response, HttpServletResponse.SC_OK, bookService.get(idFromPath(path)));
            }
        } catch (BusinessException | IllegalArgumentException exception) { writeError(response, HttpServletResponse.SC_BAD_REQUEST, exception); }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BookPayload payload = readPayload(request);
            bookService.create(payload.title(), payload.author(), payload.price(), payload.stock());
            writeJson(response, HttpServletResponse.SC_CREATED, Map.of("message", "书籍已创建"));
        } catch (BusinessException | IllegalArgumentException exception) { writeError(response, HttpServletResponse.SC_BAD_REQUEST, exception); }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BookPayload payload = readPayload(request);
            bookService.update(idFromPath(request.getPathInfo()), payload.title(), payload.author(), payload.price(), payload.stock());
            writeJson(response, HttpServletResponse.SC_OK, Map.of("message", "书籍已更新"));
        } catch (BusinessException | IllegalArgumentException exception) { writeError(response, HttpServletResponse.SC_BAD_REQUEST, exception); }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            bookService.delete(idFromPath(request.getPathInfo()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (BusinessException | IllegalArgumentException exception) { writeError(response, HttpServletResponse.SC_BAD_REQUEST, exception); }
    }

    private BookPayload readPayload(HttpServletRequest request) throws IOException {
        return JSON.readValue(request.getInputStream(), BookPayload.class);
    }

    private long idFromPath(String path) {
        try {
            long id = Long.parseLong(path.substring(1));
            if (id <= 0 || path.lastIndexOf('/') != 0) throw new NumberFormatException();
            return id;
        } catch (RuntimeException exception) { throw new IllegalArgumentException("URL 中的书籍 ID 无效"); }
    }

    private int positiveInt(String text) {
        try { return text == null ? 1 : Math.max(1, Integer.parseInt(text)); }
        catch (NumberFormatException exception) { return 1; }
    }

    private void writeError(HttpServletResponse response, int status, Exception exception) throws IOException {
        writeJson(response, status, Map.of("message", exception.getMessage()));
    }

    private void writeJson(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        JSON.writeValue(response.getWriter(), body);
    }

    private record BookPayload(String title, String author, String price, String stock) { }
}

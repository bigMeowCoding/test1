package com.example.bookstore.web;

import com.example.bookstore.service.BookService;
import com.example.bookstore.domain.book.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
class BookControllerValidationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    void createRejectsInvalidRequestBeforeCallingService() throws Exception {
        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"\",\"author\":\"张三\",\"price\":1,\"stock\":0}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("书名不能为空"));

        verifyNoInteractions(bookService);
    }

    @Test
    void getMapsDomainBookToThePublicResponseContract() throws Exception {
        when(bookService.get(7L)).thenReturn(new Book(7L, "Java 入门", "张三", new java.math.BigDecimal("59.90"), 10));

        mockMvc.perform(get("/api/books/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7))
                .andExpect(jsonPath("$.title").value("Java 入门"))
                .andExpect(jsonPath("$.price").value(59.90));
    }
}

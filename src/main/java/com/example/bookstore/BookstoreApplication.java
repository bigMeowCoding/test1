package com.example.bookstore;

import com.example.bookstore.repository.JdbcBookRepository;
import com.example.bookstore.service.BookService;
import com.example.bookstore.util.DatabaseInitializer;
import com.example.bookstore.web.BookApiServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

/**
 * Spring Boot 应用入口。
 *
 * <p>本阶段只把服务器启动和 Servlet 注册交给 Spring Boot；业务层和 JDBC 仍保持原样，
 * 便于下一步单独学习 Controller 与依赖注入。</p>
 */
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) throws Exception {
        DatabaseInitializer.initialize();
        SpringApplication.run(BookstoreApplication.class, args);
    }

    /**
     * 过渡配置：保留既有 Servlet 路由，由 Spring Boot 负责把它注册到内嵌 Tomcat。
     * 下一阶段会用 @RestController 替换这个 Servlet。
     */
    @Bean
    ServletRegistrationBean<BookApiServlet> bookApiServletRegistration() {
        BookService bookService = new BookService(new JdbcBookRepository());
        return new ServletRegistrationBean<>(new BookApiServlet(bookService), "/api/books/*");
    }
}

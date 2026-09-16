package com.example.bookstore;

import com.example.bookstore.util.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Spring Boot 应用入口。
 *
 * <p>Spring Boot 自动配置内嵌 Web 服务器，并扫描本包及其子包中的 Controller。</p>
 */
@SpringBootApplication
public class BookstoreApplication {

    public static void main(String[] args) throws Exception {
        DatabaseInitializer.initialize();
        SpringApplication.run(BookstoreApplication.class, args);
    }
}

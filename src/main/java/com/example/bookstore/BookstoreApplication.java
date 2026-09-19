package com.example.bookstore;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Spring Boot 应用入口。
 *
 * <p>Spring Boot 自动配置内嵌 Web 服务器，并扫描本包及其子包中的 Controller。</p>
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class BookstoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }
}

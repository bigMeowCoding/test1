package com.example.bookstore;

import com.example.bookstore.repository.JdbcBookRepository;
import com.example.bookstore.service.BookService;
import com.example.bookstore.util.DatabaseInitializer;
import com.example.bookstore.web.BookApiServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.nio.file.Path;

/** 书城应用入口：初始化 MySQL 数据库并注册 Servlet 路由。 */
public final class BookstoreApplication {
    private static final int PORT = 8081;

    private BookstoreApplication() { }

    public static void main(String[] args) throws Exception {
        DatabaseInitializer.initialize();
        BookService bookService = new BookService(new JdbcBookRepository());

        Tomcat tomcat = new Tomcat();
        tomcat.setPort(PORT);
        tomcat.getConnector();

        String webRoot = Path.of("src/main/webapp").toAbsolutePath().toString();
        Context context = tomcat.addContext("", webRoot);
        Tomcat.addServlet(context, "bookApiServlet", new BookApiServlet(bookService));
        context.addServletMappingDecoded("/api/books/*", "bookApiServlet");

        tomcat.start();
        System.out.printf("书城 API 已启动：http://localhost:%d/api/books%n", PORT);
        tomcat.getServer().await();
    }
}

package com.example.bookstore.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** 在程序启动时创建表并插入演示数据。 */
@Component
public final class DatabaseInitializer implements ApplicationRunner {
    private final DataSource dataSource;
    private final DatabaseProperties properties;
    private final String username;
    private final String password;

    public DatabaseInitializer(DataSource dataSource, DatabaseProperties properties,
                               @Value("${spring.datasource.username}") String username,
                               @Value("${spring.datasource.password}") String password) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) throws SQLException, IOException {
        createDatabaseIfMissing();
        String sql;
        try (var input = DatabaseInitializer.class.getResourceAsStream("/db/init.sql")) {
            if (input == null) {
                throw new IOException("找不到数据库初始化脚本：db/init.sql");
            }
            sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String[] statements = sql.split(";");
        try (Connection connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            statement.execute(statements[0]); // 先确保 books 表存在。
        }
        addStockColumnIfMissing();
        try (Connection connection = dataSource.getConnection(); var statement = connection.createStatement()) {
            for (int index = 1; index < statements.length; index++) {
                if (!statements[index].isBlank()) statement.execute(statements[index]);
            }
        }
    }

    private void createDatabaseIfMissing() throws SQLException {
        try (Connection connection = DriverManager.getConnection(properties.serverUrl(), username, password);
             var statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }

    /** 兼容不支持 ALTER TABLE ... ADD COLUMN IF NOT EXISTS 的旧版 MySQL/MariaDB。 */
    private void addStockColumnIfMissing() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (var columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "books", "stock")) {
                if (columns.next()) return;
            }
            try (var statement = connection.createStatement()) {
                statement.execute("ALTER TABLE books ADD COLUMN stock INT NOT NULL DEFAULT 0");
            }
        }
    }
}

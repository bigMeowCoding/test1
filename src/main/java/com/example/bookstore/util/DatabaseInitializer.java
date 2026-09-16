package com.example.bookstore.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

/** 在程序启动时创建表并插入演示数据。 */
public final class DatabaseInitializer {
    private DatabaseInitializer() { }

    public static void initialize() throws SQLException, IOException {
        createDatabaseIfMissing();
        String sql;
        try (var input = DatabaseInitializer.class.getResourceAsStream("/db/init.sql")) {
            if (input == null) {
                throw new IOException("找不到数据库初始化脚本：db/init.sql");
            }
            sql = new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
        String[] statements = sql.split(";");
        try (Connection connection = Database.getConnection(); var statement = connection.createStatement()) {
            statement.execute(statements[0]); // 先确保 books 表存在。
        }
        addStockColumnIfMissing();
        try (Connection connection = Database.getConnection(); var statement = connection.createStatement()) {
            for (int index = 1; index < statements.length; index++) {
                if (!statements[index].isBlank()) statement.execute(statements[index]);
            }
        }
    }

    private static void createDatabaseIfMissing() throws SQLException {
        try (Connection connection = Database.getServerConnection(); var statement = connection.createStatement()) {
            statement.execute("CREATE DATABASE IF NOT EXISTS bookstore DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");
        }
    }

    /** 兼容不支持 ALTER TABLE ... ADD COLUMN IF NOT EXISTS 的旧版 MySQL/MariaDB。 */
    private static void addStockColumnIfMissing() throws SQLException {
        try (Connection connection = Database.getConnection()) {
            try (var columns = connection.getMetaData().getColumns(connection.getCatalog(), null, "books", "stock")) {
                if (columns.next()) return;
            }
            try (var statement = connection.createStatement()) {
                statement.execute("ALTER TABLE books ADD COLUMN stock INT NOT NULL DEFAULT 0");
            }
        }
    }
}

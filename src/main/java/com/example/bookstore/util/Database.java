package com.example.bookstore.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** JDBC 连接的唯一入口。账号信息放在未纳入版本控制的 db.properties 中。 */
public final class Database {
    private static final Properties PROPERTIES = loadProperties();

    private Database() { }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(databaseUrl(), username(), password());
    }

    /** 连接到 MySQL 服务本身，用于首次创建 bookstore 数据库。 */
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(serverUrl(), username(), password());
    }

    private static String databaseUrl() { return PROPERTIES.getProperty("db.url"); }
    private static String serverUrl() { return PROPERTIES.getProperty("db.serverUrl"); }
    private static String username() { return PROPERTIES.getProperty("db.username"); }
    private static String password() { return PROPERTIES.getProperty("db.password"); }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (var input = Database.class.getResourceAsStream("/db.properties")) {
            if (input == null) throw new IllegalStateException("缺少 src/main/resources/db.properties，请由 db.properties.example 复制创建。");
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException("读取数据库配置失败", exception);
        }
    }
}

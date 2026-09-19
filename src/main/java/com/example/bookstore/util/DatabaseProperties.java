package com.example.bookstore.util;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 映射 application.yml 中 bookstore.database 下的应用专属配置。 */
@ConfigurationProperties(prefix = "bookstore.database")
public record DatabaseProperties(String serverUrl) { }

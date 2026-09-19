package com.example.bookstore.repository;

import com.example.bookstore.model.Book;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

/**
 * MyBatis Mapper：接口定义 Java 调用面，具体 SQL 位于同名的 BookMapper.xml。
 *
 * <p>{@link Param} 为 XML 中的 {@code #{keyword}} 等具名参数提供稳定名称，避免依赖编译参数。</p>
 */
@Mapper
public interface BookMapper {
    List<Book> findByKeyword(@Param("keyword") String keyword, @Param("offset") int offset,
                             @Param("limit") int limit);

    long countByKeyword(@Param("keyword") String keyword);

    Optional<Book> findById(@Param("id") long id);

    int insert(@Param("book") Book book);

    int update(@Param("book") Book book);

    int deleteById(@Param("id") long id);
}

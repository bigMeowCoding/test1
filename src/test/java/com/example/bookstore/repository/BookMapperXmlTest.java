package com.example.bookstore.repository;

import com.example.bookstore.infrastructure.persistence.BookRecord;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

/** 在不要求本机运行 MySQL 的情况下，验证 Mapper XML 格式及其语句注册。 */
class BookMapperXmlTest {
    @Test
    @SuppressWarnings("deprecation")
    void mapperXmlRegistersEveryBookCrudStatementUsingOnlyThePersistenceRecord() throws Exception {
        Configuration configuration = new Configuration();
        String resource = "mapper/BookMapper.xml";

        try (Reader reader = Resources.getResourceAsReader(resource)) {
            new XMLMapperBuilder(reader, configuration, resource, configuration.getSqlFragments(),
                    BookMapper.class.getName()).parse();
        }

        String namespace = BookMapper.class.getName();
        assertEquals(BookRecord.class, configuration.getResultMap(namespace + ".BookRecordResultMap").getType());
        assertTrue(configuration.hasStatement(namespace + ".findByKeyword"));
        assertTrue(configuration.hasStatement(namespace + ".countByKeyword"));
        assertTrue(configuration.hasStatement(namespace + ".findById"));
        assertTrue(configuration.hasStatement(namespace + ".insert"));
        assertTrue(configuration.hasStatement(namespace + ".update"));
        assertTrue(configuration.hasStatement(namespace + ".deleteById"));
    }
}

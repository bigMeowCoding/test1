package com.example.bookstore.repository;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.junit.jupiter.api.Test;

import java.io.Reader;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OrderInventoryMapperXmlTest {
    @Test
    @SuppressWarnings("deprecation")
    void orderAndInventoryMapperXmlRegisterAllStateChangingStatements() throws Exception {
        Configuration configuration = new Configuration();
        parse(configuration, "mapper/OrderMapper.xml", OrderMapper.class);
        parse(configuration, "mapper/InventoryMapper.xml", InventoryMapper.class);

        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".insertOrder"));
        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".insertLine"));
        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".findOrderById"));
        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".findPendingOrders"));
        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".findLinesByOrderId"));
        assertTrue(configuration.hasStatement(OrderMapper.class.getName() + ".updateFromPending"));
        assertTrue(configuration.hasStatement(InventoryMapper.class.getName() + ".updateAvailable"));
        assertTrue(configuration.hasStatement(InventoryMapper.class.getName() + ".reserve"));
        assertTrue(configuration.hasStatement(InventoryMapper.class.getName() + ".confirmSale"));
        assertTrue(configuration.hasStatement(InventoryMapper.class.getName() + ".release"));
    }

    private void parse(Configuration configuration, String resource, Class<?> mapper) throws Exception {
        try (Reader reader = Resources.getResourceAsReader(resource)) {
            new XMLMapperBuilder(reader, configuration, resource, configuration.getSqlFragments(), mapper.getName()).parse();
        }
    }
}

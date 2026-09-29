package com.example.bookstore.repository;

import com.example.bookstore.infrastructure.persistence.order.OrderLineRecord;
import com.example.bookstore.infrastructure.persistence.order.OrderRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {
    int insertOrder(@Param("order") OrderRecord order);
    int insertLine(@Param("line") OrderLineRecord line);
    Optional<OrderRecord> findOrderById(@Param("id") long id);
    List<OrderRecord> findPendingOrders();
    List<OrderLineRecord> findLinesByOrderId(@Param("orderId") long orderId);
    int updateFromPending(@Param("order") OrderRecord order);
}

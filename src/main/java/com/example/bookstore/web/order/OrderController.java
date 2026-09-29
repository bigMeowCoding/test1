package com.example.bookstore.web.order;

import com.example.bookstore.service.order.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public final class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody CreateOrderRequest request) {
        var items = request.items().stream().map(item -> new OrderService.OrderItem(item.bookId(), item.quantity())).toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(orderService.create(items)));
    }
    @GetMapping("/pending")
    public List<OrderResponse> pending() {
        return orderService.pending().stream().map(OrderResponse::from).toList();
    }
    @GetMapping("/{id}") public OrderResponse get(@PathVariable long id) { return OrderResponse.from(orderService.get(id)); }
    @PostMapping("/{id}/pay") public OrderResponse pay(@PathVariable long id) { return OrderResponse.from(orderService.pay(id)); }
    @PostMapping("/{id}/cancel") public OrderResponse cancel(@PathVariable long id) { return OrderResponse.from(orderService.cancel(id)); }
}

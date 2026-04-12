package com.shop.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.productservice.dto.OrderDTO;
import com.shop.productservice.entity.OrderStatus;
import com.shop.productservice.entity.ProductOrder;
import com.shop.productservice.service.OrderService;
import com.shop.productservice.dto.LocationDTO;
import com.shop.productservice.dto.TotalMoneyDTO;
import com.shop.productservice.exception.OrderNotFoundException;
import com.shop.productservice.exception.InvalidLocationException;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.math.BigDecimal;

@RestController
@RequestMapping("/orders")
public class OrderApiController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize) {
        Page<ProductOrder> page = orderService.getAllOrdersPagination(pageNo, pageSize);
        return ResponseEntity.ok(Map.of(
                "content", page.getContent(),
                "pageNo", page.getNumber(),
                "pageSize", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "first", page.isFirst(),
                "last", page.isLast()));
    }

    @GetMapping("/my-orders")
    public List<ProductOrder> getMyOrders(@RequestHeader(value = "X-User-Id", defaultValue = "default_user") String userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/total-money")
    public ResponseEntity<TotalMoneyDTO> getTotalMoneyForCurrentUser(@RequestHeader(value = "X-User-Id", defaultValue = "default_user") String userId) {
        BigDecimal total = orderService.getTotalMoneyForCurrentUser(userId);
        return ResponseEntity.ok(TotalMoneyDTO.builder().totalMoney(total).build());
    }

    @GetMapping("/user/{userId}")
    public List<ProductOrder> getByUser(@PathVariable("userId") String userId) {
        return orderService.getOrdersByUser(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchByOrderId(@RequestParam("orderId") String orderId) {
        ProductOrder order = orderService.getOrdersByOrderId(orderId.trim());
        if (order == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", "Order not found"));
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<?> placeOrder(@Valid @RequestBody OrderDTO orderReq, 
                                      @RequestHeader(value = "X-User-Id", defaultValue = "default_user") String userId) {
        try {
            orderService.saveOrder(userId, orderReq);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Order placed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable("id") Integer id, @RequestParam("st") Integer st) {
        OrderStatus[] values = OrderStatus.values();
        String statusName = null;
        for (OrderStatus os : values) {
            if (os.getId().equals(st)) {
                statusName = os.getName();
            }
        }
        if (statusName == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Bad Request", "message", "Invalid status id"));
        }
        ProductOrder updated = orderService.updateOrderStatus(id, statusName);
        if (updated == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Not Found", "message", "Order not found"));
        }
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable("id") Integer id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductOrder> getOrderById(@PathVariable("id") Integer id) {
        ProductOrder order = orderService.getOrderById(id);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrder(@PathVariable("id") Integer id, @RequestBody ProductOrder order) {
        ProductOrder updated = orderService.updateOrder(id, order);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/statuses")
    public OrderStatus[] getStatuses() {
        return OrderStatus.values();
    }

    @PutMapping("/{id}/location")
    public ResponseEntity<?> updateOrderLocation(
            @PathVariable("id") Integer id,
            @RequestBody LocationDTO locationDto) {
        try {
            orderService.updateOrderLocation(id, locationDto.getLatitude(), locationDto.getLongitude());
            return ResponseEntity.ok(java.util.Map.of("message", "Location updated successfully"));
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Not Found", "message", e.getMessage()));
        } catch (InvalidLocationException e) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Bad Request", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Error", "message", e.getMessage()));
        }
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<?> getOrderLocation(@PathVariable("id") Integer id) {
        try {
            LocationDTO location = orderService.getLocationDTO(id);
            return ResponseEntity.ok(location);
        } catch (OrderNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(java.util.Map.of("error", "Not Found", "message", e.getMessage()));
        }
    }
}

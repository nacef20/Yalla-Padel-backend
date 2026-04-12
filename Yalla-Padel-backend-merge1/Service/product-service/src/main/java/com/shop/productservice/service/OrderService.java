package com.shop.productservice.service;

import com.shop.productservice.dto.OrderDTO;
import com.shop.productservice.entity.ProductOrder;
import com.shop.productservice.dto.LocationDTO;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    void saveOrder(String userId, OrderDTO orderRequest) throws Exception;

    List<ProductOrder> getOrdersByUser(String userId);

    ProductOrder updateOrderStatus(Integer id, String status);

    List<ProductOrder> getAllOrders();

    ProductOrder getOrdersByOrderId(String orderId);

    Page<ProductOrder> getAllOrdersPagination(Integer pageNo, Integer pageSize);

    void deleteOrder(Integer id);

    ProductOrder getOrderById(Integer id);

    ProductOrder updateOrder(Integer id, ProductOrder order);

    BigDecimal getTotalMoneyForCurrentUser(String userId);

    BigDecimal getTotalMoneyForAllUsers();

    LocationDTO getLocationDTO(Integer orderId);

    void updateOrderLocation(Integer orderId, Double latitude, Double longitude);
}

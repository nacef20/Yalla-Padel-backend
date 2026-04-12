package com.shop.productservice.repository;

import com.shop.productservice.entity.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductOrderRepo extends JpaRepository<ProductOrder, Integer> {

    ProductOrder findByOrderId(String orderId);

    List<ProductOrder> findByUserId(String userId);

    @Query("SELECT COALESCE(SUM(o.price * o.quantity), 0) FROM ProductOrder o WHERE o.userId = :userId")
    Double sumTotalAmountByUserId(@Param("userId") String userId);

    @Query("SELECT COALESCE(SUM(o.price * o.quantity), 0) FROM ProductOrder o")
    Double sumTotalAmountAllUsers();
}

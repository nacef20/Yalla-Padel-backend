package com.shop.productservice.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.shop.productservice.entity.ProductOrder;

import java.util.List;

public interface InventoryRepository extends JpaRepository<ProductOrder, Integer> {

    @Query("SELECT po.product, SUM(po.quantity) AS totalQty, SUM(po.price * po.quantity) AS totalRev " +
            "FROM ProductOrder po " +
            "GROUP BY po.product.id " +
            "ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProducts(Pageable pageable);

    @Query("SELECT po.product, SUM(po.quantity) AS totalQty, SUM(po.price * po.quantity) AS totalRev " +
            "FROM ProductOrder po " +
            "GROUP BY po.product.id " +
            "ORDER BY totalQty DESC")
    List<Object[]> findAllProductSales();

    @Query("SELECT COALESCE(SUM(po.quantity), 0) FROM ProductOrder po WHERE po.product.id = :productId")
    Long findTotalQuantitySoldByProduct(@Param("productId") Integer productId);

    @Query("SELECT COALESCE(AVG(po.quantity), 0) FROM ProductOrder po WHERE po.product.id = :productId")
    Double findAverageQuantitySoldPerOrder(@Param("productId") Integer productId);

    @Query("SELECT COUNT(po) FROM ProductOrder po WHERE po.product.id = :productId")
    Long countOrdersByProduct(@Param("productId") Integer productId);
}

package com.shop.productservice.service;

import com.shop.productservice.dto.InventoryAlertDTO;
import com.shop.productservice.dto.InventoryReportDTO;
import com.shop.productservice.dto.ProductSalesDTO;
import com.shop.productservice.dto.RestockResponseDTO;

import java.util.List;

public interface InventoryService {

    List<InventoryAlertDTO> getLowStockAlerts();

    List<ProductSalesDTO> getTopSellingProducts(int limit);

    InventoryReportDTO generateInventoryReport();

    int calculateRecommendedRestock(Integer productId);

    RestockResponseDTO restockProduct(Integer productId, Integer quantity);
}

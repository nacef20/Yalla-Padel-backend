package com.shop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryReportDTO {
    private List<ProductSalesDTO> fastMovingProducts;
    private List<ProductSalesDTO> slowMovingProducts;
    private List<InventoryAlertDTO> outOfStockProducts;
    private LocalDateTime generatedAt;
}

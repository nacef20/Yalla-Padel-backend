package com.shop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertDTO {
    private Integer productId;
    private String productTitle;
    private int currentStock;
    private int minStockLevel;
    private int deficit;
}

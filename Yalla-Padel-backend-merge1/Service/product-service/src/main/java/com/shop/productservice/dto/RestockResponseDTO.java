package com.shop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RestockResponseDTO {
    private Integer productId;
    private String productTitle;
    private int previousStock;
    private int addedQuantity;
    private int newStock;
    private int recommendedRestockQuantity;
    private LocalDateTime restockedAt;
}

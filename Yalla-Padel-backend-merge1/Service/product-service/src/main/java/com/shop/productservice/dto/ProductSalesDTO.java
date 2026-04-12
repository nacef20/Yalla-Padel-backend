package com.shop.productservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSalesDTO {
    private Integer productId;
    private String productTitle;
    private Long totalQuantitySold;
    private Double totalRevenue;
}

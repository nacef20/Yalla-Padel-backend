package com.shop.productservice.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Integer id;
    private String title;
    private String description;
    private String category;
    private Double price;
    private int stock;
    private int minStockLevel;
    private String image;
    private int discount;
    private Double discountPrice;
    private Boolean isActive;
}

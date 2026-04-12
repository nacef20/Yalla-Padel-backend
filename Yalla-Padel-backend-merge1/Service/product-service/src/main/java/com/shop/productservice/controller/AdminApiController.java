package com.shop.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.shop.productservice.repository.CategoryRepo;
import com.shop.productservice.repository.ProductOrderRepo;
import com.shop.productservice.repository.ProductRepo;

import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminApiController {

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private ProductOrderRepo orderRepo;

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return Map.of(
                "totalProducts", productRepo.count(),
                "totalCategories", categoryRepo.count(),
                "totalOrders", orderRepo.count());
    }
}

package com.shop.productservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.productservice.dto.InventoryAlertDTO;
import com.shop.productservice.dto.InventoryReportDTO;
import com.shop.productservice.dto.ProductSalesDTO;
import com.shop.productservice.dto.RestockResponseDTO;
import com.shop.productservice.service.InventoryService;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryApiController {

    private final InventoryService inventoryService;

    @GetMapping("/alerts")
    public ResponseEntity<List<InventoryAlertDTO>> getLowStockAlerts() {
        log.info("GET /inventory/alerts — fetching low-stock alerts");
        List<InventoryAlertDTO> alerts = inventoryService.getLowStockAlerts();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<ProductSalesDTO>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        log.info("GET /inventory/top-products — limit={}", limit);
        List<ProductSalesDTO> topProducts = inventoryService.getTopSellingProducts(limit);
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/report")
    public ResponseEntity<InventoryReportDTO> getInventoryReport() {
        log.info("GET /inventory/report — generating inventory report");
        InventoryReportDTO report = inventoryService.generateInventoryReport();
        return ResponseEntity.ok(report);
    }

    @PostMapping("/restock/{productId}")
    public ResponseEntity<RestockResponseDTO> restockProduct(
            @PathVariable Integer productId,
            @RequestParam(required = false) Integer quantity) {
        log.info("POST /inventory/restock/{} — quantity={}", productId,
                quantity != null ? quantity : "auto");
        RestockResponseDTO response = inventoryService.restockProduct(productId, quantity);
        return ResponseEntity.ok(response);
    }
}

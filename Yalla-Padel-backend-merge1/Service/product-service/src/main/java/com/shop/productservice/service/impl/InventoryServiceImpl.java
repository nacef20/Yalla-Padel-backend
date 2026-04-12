package com.shop.productservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shop.productservice.entity.Product;
import com.shop.productservice.dto.InventoryAlertDTO;
import com.shop.productservice.dto.InventoryReportDTO;
import com.shop.productservice.dto.ProductSalesDTO;
import com.shop.productservice.dto.RestockResponseDTO;
import com.shop.productservice.repository.InventoryRepository;
import com.shop.productservice.repository.ProductRepo;
import com.shop.productservice.service.InventoryService;
import com.shop.productservice.exception.InvalidInputException;
import com.shop.productservice.exception.ProductNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class InventoryServiceImpl implements InventoryService {

    private static final int RESTOCK_MULTIPLIER = 3;

    private final InventoryRepository inventoryRepository;
    private final ProductRepo productRepo;

    @Override
    public List<InventoryAlertDTO> getLowStockAlerts() {
        log.info("Generating low-stock alerts");
        List<Product> lowStockProducts = productRepo.findProductsBelowMinStock();
        return lowStockProducts.stream()
                .map(this::mapToAlertDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductSalesDTO> getTopSellingProducts(int limit) {
        if (limit <= 0) {
            throw new InvalidInputException("Limit must be a positive integer, but got: " + limit);
        }
        log.info("Fetching top {} selling products", limit);
        List<Object[]> results = inventoryRepository.findTopSellingProducts(PageRequest.of(0, limit));
        return results.stream()
                .map(this::mapToSalesDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryReportDTO generateInventoryReport() {
        log.info("Generating comprehensive inventory report");

        List<Object[]> allSales = inventoryRepository.findAllProductSales();
        List<ProductSalesDTO> allSalesDTOs = allSales.stream()
                .map(this::mapToSalesDTO)
                .collect(Collectors.toList());

        List<ProductSalesDTO> fastMoving = new ArrayList<>();
        List<ProductSalesDTO> slowMoving = new ArrayList<>();

        if (!allSalesDTOs.isEmpty()) {
            long medianQuantity = calculateMedianQuantity(allSalesDTOs);
            for (ProductSalesDTO dto : allSalesDTOs) {
                if (dto.getTotalQuantitySold() > medianQuantity) {
                    fastMoving.add(dto);
                } else {
                    slowMoving.add(dto);
                }
            }
        }

        List<Product> outOfStock = productRepo.findOutOfStockProducts();
        List<InventoryAlertDTO> outOfStockAlerts = outOfStock.stream()
                .map(this::mapToAlertDTO)
                .collect(Collectors.toList());

        return InventoryReportDTO.builder()
                .fastMovingProducts(fastMoving)
                .slowMovingProducts(slowMoving)
                .outOfStockProducts(outOfStockAlerts)
                .generatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public int calculateRecommendedRestock(Integer productId) {
        if (productId == null) {
            throw new InvalidInputException("Product ID must not be null");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        Double avgPerOrder = inventoryRepository.findAverageQuantitySoldPerOrder(productId);
        int salesBasedTarget = (int) Math.ceil((avgPerOrder != null ? avgPerOrder : 0.0) * RESTOCK_MULTIPLIER);
        int target = Math.max(product.getMinStockLevel(), salesBasedTarget);
        int recommended = target - product.getStock();

        if (recommended <= 0) {
            recommended = product.getMinStockLevel();
        }

        log.info("Restock recommendation for product {}: {} units", productId, recommended);
        return recommended;
    }

    @Override
    @Transactional
    public RestockResponseDTO restockProduct(Integer productId, Integer quantity) {
        if (productId == null) {
            throw new InvalidInputException("Product ID must not be null");
        }

        Product product = productRepo.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        int recommended = calculateRecommendedRestock(productId);
        int addQuantity;

        if (quantity != null) {
            if (quantity <= 0) {
                throw new InvalidInputException("Restock quantity must be positive, but got: " + quantity);
            }
            addQuantity = quantity;
        } else {
            addQuantity = recommended;
        }

        int previousStock = product.getStock();
        product.setStock(previousStock + addQuantity);
        productRepo.save(product);

        log.info("Restocked product {}: {} → {} (+{} units)", productId, previousStock, product.getStock(), addQuantity);

        return RestockResponseDTO.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .previousStock(previousStock)
                .addedQuantity(addQuantity)
                .newStock(product.getStock())
                .recommendedRestockQuantity(recommended)
                .restockedAt(LocalDateTime.now())
                .build();
    }

    private InventoryAlertDTO mapToAlertDTO(Product product) {
        return InventoryAlertDTO.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .currentStock(product.getStock())
                .minStockLevel(product.getMinStockLevel())
                .deficit(product.getMinStockLevel() - product.getStock())
                .build();
    }

    private ProductSalesDTO mapToSalesDTO(Object[] row) {
        Product product = (Product) row[0];
        Long totalQty = (Long) row[1];
        Double totalRev = (Double) row[2];

        return ProductSalesDTO.builder()
                .productId(product.getId())
                .productTitle(product.getTitle())
                .totalQuantitySold(totalQty)
                .totalRevenue(totalRev)
                .build();
    }

    private long calculateMedianQuantity(List<ProductSalesDTO> salesList) {
        int size = salesList.size();
        if (size == 0) return 0L;
        if (size % 2 == 1) {
            return salesList.get(size / 2).getTotalQuantitySold();
        } else {
            long lower = salesList.get(size / 2 - 1).getTotalQuantitySold();
            long upper = salesList.get(size / 2).getTotalQuantitySold();
            return (lower + upper) / 2;
        }
    }
}

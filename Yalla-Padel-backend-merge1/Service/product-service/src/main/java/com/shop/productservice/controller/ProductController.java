package com.shop.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.shop.productservice.dto.ProductDTO;
import com.shop.productservice.service.ProductService;

import java.util.Map;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") int pageSize,
            @RequestParam(name = "search", defaultValue = "") String search) {
        Page<ProductDTO> page;
        if (search != null && !search.isBlank()) {
            page = productService.searchProductPagination(pageNo, pageSize, search);
        } else {
            page = productService.getAllProductsPagination(pageNo, pageSize);
        }
        return ResponseEntity.ok(Map.of(
                "content", page.getContent(),
                "pageNo", page.getNumber(),
                "pageSize", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "first", page.isFirst(),
                "last", page.isLast()));
    }

    @GetMapping("/active")
    public ResponseEntity<Map<String, Object>> getActive(
            @RequestParam(name = "pageNo", defaultValue = "0") int pageNo,
            @RequestParam(name = "pageSize", defaultValue = "12") int pageSize,
            @RequestParam(name = "category", defaultValue = "") String category,
            @RequestParam(name = "search", defaultValue = "") String search) {
        Page<ProductDTO> page;
        if (search != null && !search.isEmpty()) {
            page = productService.searchActiveProductPagination(pageNo, pageSize, category, search);
        } else {
            page = productService.getAllActiveProductPagination(pageNo, pageSize, category);
        }
        return ResponseEntity.ok(Map.of(
                "content", page.getContent(),
                "pageNo", page.getNumber(),
                "pageSize", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages(),
                "first", page.isFirst(),
                "last", page.isLast()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable("id") int id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<ProductDTO> create(
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProductWithImage(productDTO, image));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.saveProduct(productDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDTO> update(
            @PathVariable("id") int id,
            @RequestPart("product") ProductDTO productDTO,
            @RequestPart(value = "imageFile", required = false) org.springframework.web.multipart.MultipartFile image) {
        productDTO.setId(id);
        if (image != null && !image.isEmpty()) {
            return ResponseEntity.ok(productService.updateProductWithImage(productDTO, image));
        }
        return ResponseEntity.ok(productService.updateProduct(productDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        if (productService.deleteProduct(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

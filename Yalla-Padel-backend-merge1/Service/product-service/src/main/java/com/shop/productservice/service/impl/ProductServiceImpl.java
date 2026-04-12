package com.shop.productservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import com.shop.productservice.dto.ProductDTO;
import com.shop.productservice.entity.Product;
import com.shop.productservice.exception.ProductNotFoundException;
import com.shop.productservice.repository.ProductRepo;
import com.shop.productservice.repository.CartRepo;
import com.shop.productservice.service.ProductService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepo productRepository;

    @Autowired
    private CartRepo cartRepository;

    @Override
    public ProductDTO saveProduct(ProductDTO productDTO) {
        Product product = mapToEntity(productDTO);
        calculateDiscountPrice(product);
        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    @Override
    public ProductDTO saveProductWithImage(ProductDTO productDTO, MultipartFile image) {
        Product product = mapToEntity(productDTO);
        if (image != null && !image.isEmpty()) {
            product.setImage(image.getOriginalFilename());
            saveImage(image);
        }
        calculateDiscountPrice(product);
        Product saved = productRepository.save(product);
        return mapToDTO(saved);
    }

    private void calculateDiscountPrice(Product product) {
        if (product.getPrice() != null && product.getDiscount() != null) {
            Double discount = product.getPrice() * (product.getDiscount() / 100.0);
            Double discountPrice = product.getPrice() - discount;
            product.setDiscountPrice(discountPrice);
        } else {
            product.setDiscountPrice(product.getPrice());
        }
    }

    private void saveImage(MultipartFile image) {
        try {
            String uploadDir = "uploads/product_img";
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(image.getOriginalFilename());
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> getAllProductsPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findAll(pageable).map(this::mapToDTO);
    }

    @Override
    public ProductDTO getProductById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }

    @Override
    public ProductDTO updateProduct(ProductDTO productDTO) {
        Product dbProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productDTO.getId()));

        dbProduct.setTitle(productDTO.getTitle());
        dbProduct.setDescription(productDTO.getDescription());
        dbProduct.setCategory(productDTO.getCategory());
        dbProduct.setPrice(productDTO.getPrice());
        dbProduct.setStock(productDTO.getStock());
        dbProduct.setImage(productDTO.getImage());
        dbProduct.setIsActive(productDTO.getIsActive());
        dbProduct.setDiscount(productDTO.getDiscount());
        calculateDiscountPrice(dbProduct);

        Product updated = productRepository.save(dbProduct);
        return mapToDTO(updated);
    }

    @Override
    public ProductDTO updateProductWithImage(ProductDTO productDTO, MultipartFile image) {
        Product dbProduct = productRepository.findById(productDTO.getId())
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productDTO.getId()));

        String imageName = (image == null || image.isEmpty()) ? dbProduct.getImage() : image.getOriginalFilename();

        dbProduct.setTitle(productDTO.getTitle());
        dbProduct.setDescription(productDTO.getDescription());
        dbProduct.setCategory(productDTO.getCategory());
        dbProduct.setPrice(productDTO.getPrice());
        dbProduct.setStock(productDTO.getStock());
        dbProduct.setImage(imageName);
        dbProduct.setIsActive(productDTO.getIsActive());
        dbProduct.setDiscount(productDTO.getDiscount());
        calculateDiscountPrice(dbProduct);

        Product updated = productRepository.save(dbProduct);

        if (!ObjectUtils.isEmpty(updated) && image != null && !image.isEmpty()) {
            saveImage(image);
        }
        return mapToDTO(updated);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public Boolean deleteProduct(Integer id) {
        if (productRepository.existsById(id)) {
            cartRepository.deleteByProductId(id);
            productRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<ProductDTO> getAllActiveProducts(String category) {
        List<Product> products;
        if (category == null || category.isEmpty()) {
            products = productRepository.findByIsActiveTrue();
        } else {
            products = productRepository.findByCategory(category);
        }
        return products.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProduct(String ch) {
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<ProductDTO> searchProductPagination(Integer pageNo, Integer pageSize, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable)
                .map(this::mapToDTO);
    }

    @Override
    public Page<ProductDTO> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        if (category == null || category.isEmpty()) {
            return productRepository.findByIsActiveTrue(pageable).map(this::mapToDTO);
        } else {
            return productRepository.findByCategory(pageable, category).map(this::mapToDTO);
        }
    }

    @Override
    public Page<ProductDTO> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return productRepository.findByIsActiveTrueAndSearch(ch, pageable).map(this::mapToDTO);
    }

    private ProductDTO mapToDTO(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .title(product.getTitle())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .stock(product.getStock())
                .minStockLevel(product.getMinStockLevel())
                .image(product.getImage())
                .discount(product.getDiscount())
                .discountPrice(product.getDiscountPrice())
                .isActive(product.getIsActive())
                .build();
    }

    private Product mapToEntity(ProductDTO dto) {
        return Product.builder()
                .id(dto.getId())
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .price(dto.getPrice())
                .stock(dto.getStock())
                .minStockLevel(dto.getMinStockLevel())
                .image(dto.getImage())
                .discount(dto.getDiscount())
                .isActive(dto.getIsActive())
                .build();
    }
}

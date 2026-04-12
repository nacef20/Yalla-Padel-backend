package com.shop.productservice.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import com.shop.productservice.dto.ProductDTO;
import java.util.List;

public interface ProductService {
    ProductDTO saveProduct(ProductDTO productDTO);
    ProductDTO saveProductWithImage(ProductDTO productDTO, MultipartFile image);
    List<ProductDTO> getAllProducts();
    Page<ProductDTO> getAllProductsPagination(Integer pageNo, Integer pageSize);
    ProductDTO getProductById(Integer id);
    ProductDTO updateProduct(ProductDTO productDTO);
    ProductDTO updateProductWithImage(ProductDTO productDTO, MultipartFile image);
    Boolean deleteProduct(Integer id);
    List<ProductDTO> getAllActiveProducts(String category);
    List<ProductDTO> searchProduct(String ch);
    Page<ProductDTO> searchProductPagination(Integer pageNo, Integer pageSize, String ch);
    Page<ProductDTO> getAllActiveProductPagination(Integer pageNo, Integer pageSize, String category);
    Page<ProductDTO> searchActiveProductPagination(Integer pageNo, Integer pageSize, String category, String ch);
}

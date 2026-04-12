package com.shop.productservice.service;

import org.springframework.data.domain.Page;
import com.shop.productservice.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    CategoryDTO saveCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> getAllCategory();
    Boolean existCategory(String name);
    Boolean deleteCategory(int id);
    CategoryDTO getCategoryById(int id);
    List<CategoryDTO> getAllActiveCategory();
    Page<CategoryDTO> getAllCategorPagination(Integer pageNo, Integer pageSize);
}

package com.shop.productservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.shop.productservice.dto.CategoryDTO;
import com.shop.productservice.entity.Category;
import com.shop.productservice.repository.CategoryRepo;
import com.shop.productservice.service.CategoryService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepo categoryRepo;

    @Override
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        Category category = mapToEntity(categoryDTO);
        Category saved = categoryRepo.save(category);
        return mapToDTO(saved);
    }

    @Override
    public List<CategoryDTO> getAllCategory() {
        return categoryRepo.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Boolean existCategory(String name) {
        return categoryRepo.existsByName(name);
    }

    @Override
    public Boolean deleteCategory(int id) {
        if (categoryRepo.existsById(id)) {
            categoryRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public CategoryDTO getCategoryById(int id) {
        Category category = categoryRepo.findById(id).orElse(null);
        return category != null ? mapToDTO(category) : null;
    }

    @Override
    public List<CategoryDTO> getAllActiveCategory() {
        return categoryRepo.findByIsActiveTrueOrIsActiveIsNull().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public Page<CategoryDTO> getAllCategorPagination(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return categoryRepo.findAll(pageable).map(this::mapToDTO);
    }

    private CategoryDTO mapToDTO(Category category) {
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .imageName(category.getImageName())
                .isActive(category.getIsActive())
                .build();
    }

    private Category mapToEntity(CategoryDTO dto) {
        return Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .imageName(dto.getImageName())
                .isActive(dto.getIsActive())
                .build();
    }
}

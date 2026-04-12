package com.shop.productservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.shop.productservice.entity.Category;

import java.util.List;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Integer> {

    Boolean existsByName(String name);

    List<Category> findByIsActiveTrueOrIsActiveIsNull();
}

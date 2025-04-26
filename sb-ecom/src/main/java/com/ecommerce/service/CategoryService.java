package com.ecommerce.service;


import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;



public interface CategoryService {
    CategoryResponse getCategories(Integer pageNumber, Integer pageSize);
    CategoryDTO createCategory(CategoryDTO categoryDTO);
    CategoryDTO deleteCategory(long categoryId);
    CategoryDTO updateCategory(long categoryId, CategoryDTO categoryDTO);
}

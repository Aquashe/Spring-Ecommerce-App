package com.ecommerce.service;

import com.ecommerce.exceptions.APIException;
import com.ecommerce.exceptions.ResourceNotFoundException;
import com.ecommerce.model.Category;
import com.ecommerce.payload.CategoryDTO;
import com.ecommerce.payload.CategoryResponse;
import com.ecommerce.repository.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
public class CategoryServiceImplementation implements  CategoryService{
    //private List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getCategories(Integer pageNumber, Integer pageSize) {
        List<Category> categories = categoryRepository.findAll();
        if (categories.isEmpty())
            throw new APIException("Category");
        List<CategoryDTO> categoryDTOS =  categories.stream().map(category -> modelMapper
                .map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        return  categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategoryFromDB = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategoryFromDB != null)
            throw new APIException("Category",category.getCategoryName());
        Category savedCategory = categoryRepository.save(category);
        return  modelMapper.map(savedCategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(long categoryId) {
        List<Category> categories = categoryRepository.findAll();
        Category category = categories.stream()
                .filter(c-> c.getCategoryId() == categoryId)
                .findFirst()
                .orElseThrow(
                        //()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category "+categoryId+" not found")
                        () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
                );

        categoryRepository.delete(category);
        return modelMapper.map(category, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(long categoryId, CategoryDTO categoryDTO) {
        Optional<Category> savedOptionalCategoryFromDB = categoryRepository.findById(categoryId);
        Category savedCategoryFromDB = savedOptionalCategoryFromDB
                .orElseThrow(()-> new ResourceNotFoundException("Category","categoryId",categoryId));

        Category category = modelMapper.map(categoryDTO, Category.class);
        savedCategoryFromDB.setCategoryName(category.getCategoryName());
        Category savedCategory = categoryRepository.save(savedCategoryFromDB);
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

}

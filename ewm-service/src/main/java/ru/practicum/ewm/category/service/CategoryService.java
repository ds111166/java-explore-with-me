package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(CategoryRequestDto newCategory);

    void deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryRequestDto updateCategory);

    List<CategoryDto> getCategories(Integer size, Integer from);

    CategoryDto getCategoryById(Long catId);
}

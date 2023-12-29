package ru.practicum.ewm.category.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.model.Category;

@Component
public class CategoryMapper {
    public Category toCategory(CategoryRequestDto newCategory) {
        return Category.builder().name(newCategory.getName()).build();
    }

    public CategoryDto toCategoryDto(Category createdCategory) {
        return CategoryDto.builder()
                .id(createdCategory.getId())
                .name(createdCategory.getName())
                .build();
    }
}

package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto createCategory(CategoryRequestDto newCategory) {
        try {
            Category category = categoryMapper.toCategory(newCategory);
            final Category createdCategory = categoryRepository.saveAndFlush(category);
            return categoryMapper.toCategoryDto(createdCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }

    }

    @Override
    @Transactional
    public void deleteCategory(Long catId) {
        categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        //если есть событие связанное с удаляемой категорией
        if (eventRepository.existsByCategoryId(catId)) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryRequestDto updateCategoryDto) {
        Category updateCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        final String newCategoryName = updateCategoryDto.getName();
        String categoryName = updateCategory.getName();
        if (newCategoryName != null && !Objects.equals(categoryName, newCategoryName)) {
            checkingCategoryName(newCategoryName);
            updateCategory.setName(newCategoryName);
        }
        try {
            Category updatedCategory = categoryRepository.saveAndFlush(updateCategory);
            return categoryMapper.toCategoryDto(updatedCategory);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public List<CategoryDto> getCategories(Integer size, Integer from) {
        final Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + catId + " was not found"));
        return categoryMapper.toCategoryDto(category);
    }

    private void checkingCategoryName(String categoryName) {
        if (categoryRepository.existsCategoryByName(categoryName)) {
            throw new ConflictException("Категоия с name='" + categoryName + "' уже существует");
        }
    }
}

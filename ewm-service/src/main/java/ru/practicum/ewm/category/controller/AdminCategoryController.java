package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(
            @Valid @RequestBody CategoryRequestDto newCategory
    ) {
        log.info("Добавление новой категории: newCategory=\"{}\"", newCategory);
        final CategoryDto createdCompilation = categoryService.createCategory(newCategory);
        log.info("Добавлена категория: \"{}\"", createdCompilation);
        return createdCompilation;
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(
            @PathVariable @NotNull Long catId
    ) {
        log.info("Удаление категории catId={}", catId);
        categoryService.deleteCategory(catId);
        log.info("Удалена категория catId={}", catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable @NotNull Long catId,
            @Valid @RequestBody CategoryRequestDto updateCategory
    ) {
        log.info("Обновление категории catId={}, updateCategory=\"{}\"", catId, updateCategory);
        final CategoryDto updatedCategory = categoryService.updateCategory(catId, updateCategory);
        log.info("Обновлена категория: \"{}\"", updatedCategory);
        return updatedCategory;
    }
}

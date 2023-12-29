package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilation(
            @Valid @RequestBody NewCompilationDto newCompilation
    ) {
        log.info("Добавление новой подборки: newCompilation=\"{}\"", newCompilation);
        final CompilationDto createdCompilation = compilationService.createCompilation(newCompilation);
        log.info("Добавлена поборка: \"{}\"", createdCompilation);
        return createdCompilation;
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(
            @PathVariable @NotNull Long compId
    ) {
        log.info("Удаление подборки compId={}", compId);
        compilationService.deleteCompilation(compId);
        log.info("Удалена подборка compId={}", compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto updateCompilation(
            @PathVariable @NotNull Long compId,
            @Valid @RequestBody UpdateCompilationDto updateCompilation
    ) {
        log.info("Обновление подборки compId={}, updateCompilation=\"{}\"", compId, updateCompilation);
        final CompilationDto updatedCompilation = compilationService.updateCompilation(compId, updateCompilation);
        log.info("Обновлена поборка: \"{}\"", updatedCompilation);
        return updatedCompilation;
    }
}

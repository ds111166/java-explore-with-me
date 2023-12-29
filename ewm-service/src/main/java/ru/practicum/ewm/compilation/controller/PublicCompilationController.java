package ru.practicum.ewm.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class PublicCompilationController {
    private final CompilationService compilationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CompilationDto> getCompilations(
            @RequestParam(required = false) Boolean pinned,
            @RequestParam(defaultValue = "10", required = false) Integer size,
            @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение подборок событий: pinned={}, size={}, from={}", pinned, size, from);
        final List<CompilationDto> compilations = compilationService.getCompilations(pinned, size, from);
        log.info("Return compilations = \"{}\"", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto getCompilationById(
            @PathVariable @NotNull Long compId
    ) {
        log.info("Получение подбороки событий compId={}", compId);
        final CompilationDto compilation = compilationService.getCompilationById(compId);
        log.info("Return compilation = \"{}\"", compilation);
        return compilation;
    }
}

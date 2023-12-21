package ru.practicum.ewm.stats.server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;
import ru.practicum.ewm.stats.server.service.StatsService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @GetMapping("/stats")
    @ResponseStatus(HttpStatus.OK)
    public List<ViewStatsDto> getStats(
            @Valid @RequestParam @TimestampHitValidate String start,
            @Valid @RequestParam @TimestampHitValidate String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Получение статистики: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<ViewStatsDto> viewsStatsDto = statsService.getStats(start, end, uris, unique);
        log.info("Return viewStatsDto = \"{}\"", viewsStatsDto);
        return viewsStatsDto;
    }

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveHit(@Valid @RequestBody EndpointHitDto endpointHit) {
        log.info("Сохранение информации об endpointHit=\"{}\"", endpointHit);
        statsService.saveHit(endpointHit);
        log.info("Информации об endpointHit=\"{}\" сохранена", endpointHit);
    }
}

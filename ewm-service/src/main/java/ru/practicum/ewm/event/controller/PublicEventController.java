package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/events")
public class PublicEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getPublicEvents(
            @RequestParam(required = false)
            String text, //текст для поиска в аннотации и подробном описании события
            @RequestParam(required = false)
            List<Long> categories,
            @RequestParam(required = false)
            Boolean paid, //поиск только платных/бесплатных событий
            @RequestParam(required = false)
            String rangeStart, //дата и время не раньше которых должно произойти событие
            @RequestParam(required = false)
            String rangeEnd, //дата и время не позже которых должно произойти событие
            @RequestParam(required = false, defaultValue = "false")
            Boolean onlyAvailable, //только события у которых не исчерпан лимит запросов на участие
            @RequestParam(required = false)
            String sort, //сортировки: по дате события или по количеству просмотров (EVENT_DATE, VIEWS)
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false)
            Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false)
            Integer from,
            HttpServletRequest request
    ) {
        log.info("Получение публичных событий: text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, " +
                        "onlyAvailable={}, sort={}, size={}, from={}",
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                size,
                from);
        List<EventShortDto> publicEvents = eventService.getPublicEvents(
                request,
                text,
                categories,
                paid,
                rangeStart,
                rangeEnd,
                onlyAvailable,
                sort,
                size,
                from);
        log.info("Return public events = \"{}\"", publicEvents);
        return publicEvents;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getPublicEventById(
            @PathVariable @NotNull Long id,
            HttpServletRequest request
    ) {
        log.info("Получение публичного события по id={}", id);
        EventFullDto publicEvent = eventService.getPublicEventById(request, id);
        log.info("Return public event = \"{}\"", publicEvent);
        return publicEvent;
    }
}

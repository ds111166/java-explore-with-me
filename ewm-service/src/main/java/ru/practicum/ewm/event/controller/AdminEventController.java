package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventFullDto> getEvents(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение событий: users={}, states={}, categories={}," +
                        " rangeStart={}, rangeEnd={}. size={}, from={}",
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                size,
                from);
        final List<EventFullDto> events = eventService.getEvents(
                users,
                states,
                categories,
                rangeStart,
                rangeEnd,
                size,
                from);
        log.info("Return events = \"{}\"", events);
        return events;
    }

    @PatchMapping("/{eventId}")
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto updateEvent(
            @PathVariable @NotNull Long eventId,
            @Valid @RequestBody @NotNull UpdateEventAdminRequest updateEventRequest
    ) {
        log.info("Обновление события: eventId={}, updateEventRequest={}", eventId, updateEventRequest);
        EventFullDto updatedEvent = eventService.updateEvent(eventId, updateEventRequest);
        log.info("Обновлено событие: \"{}\"", updatedEvent);
        return updatedEvent;
    }
}

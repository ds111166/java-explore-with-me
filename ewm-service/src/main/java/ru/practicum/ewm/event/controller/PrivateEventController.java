package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
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
@RequestMapping(path = "/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getEventsOfCurrentUser(
            @PathVariable @NotNull Long userId,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false)
            Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false)
            Integer from
    ) {
        log.info("Получить события пользователя userId={}, size={}, from={}", userId, size, from);
        List<EventShortDto> eventsOfCurrentUser = eventService.getEventsOfUser(userId, size, from);
        log.info("Return eventsOfCurrentUser = \"{}\"", eventsOfCurrentUser);
        return eventsOfCurrentUser;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEvent(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody @NotNull NewEventDto newEvent
    ) {
        log.info("Создать событие пользователя userId={}, newEvent={}", userId, newEvent);
        EventFullDto createdEvent = eventService.createEvent(userId, newEvent);
        log.info("Создано событие createdEvent={}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventOfCurrentUserByEventId(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId
    ) {
        log.info("Получить событие пользователя userId={} по eventId={}", userId, eventId);
        EventFullDto eventOfCurrentUserByEventId = eventService.getEventOfCurrentUserByEventId(userId, eventId);
        log.info("Return eventOfCurrentUserByEventId = \"{}\"", eventOfCurrentUserByEventId);
        return eventOfCurrentUserByEventId;
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.OnUpdate.class})
    public EventFullDto updateEvent(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId,
            @Valid @RequestBody @NotNull UpdateEventUserRequest updateEvent
    ) {
        log.info("Измененить событие пользователя userId={}, eventId={}, updateEvent={}",
                userId, eventId, updateEvent);
        EventFullDto updatedEventOfUser = eventService.updateEventOfUser(userId, eventId, updateEvent);
        log.info("Изменено событие пользователя userId={}, eventId={}, updatedEventOfUser={}",
                userId, eventId, updatedEventOfUser);
        return updatedEventOfUser;
    }
}

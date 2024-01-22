package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventGetPublicResponse;
import ru.practicum.ewm.event.dto.EventResponseDto;
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
    public List<EventGetPublicResponse> getEventsOfCurrentUser(
            @PathVariable @NotNull Long userId,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получить события пользователя userId={}, size={}, from={}", userId, size, from);
        List<EventGetPublicResponse> eventsOfCurrentUser = eventService.getEventsOfUser(userId, size, from);
        log.info("Return eventsOfCurrentUser = \"{}\"", eventsOfCurrentUser);
        return eventsOfCurrentUser;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public EventResponseDto createEvent(
            @PathVariable @NotNull Long userId,
            @Valid @RequestBody @NotNull NewEventDto newEvent
    ) {
        log.info("Создать событие пользователя userId={}, newEvent={}", userId, newEvent);
        EventResponseDto createdEvent = eventService.createEvent(userId, newEvent);
        log.info("Создано событие createdEvent={}", createdEvent);
        return createdEvent;
    }

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventResponseDto getEventOfCurrentUserByEventId(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId
    ) {
        log.info("Получить событие пользователя userId={} по eventId={}", userId, eventId);
        EventResponseDto eventOfCurrentUserByEventId = eventService.getEventOfCurrentUserByEventId(userId, eventId);
        log.info("Return eventOfCurrentUserByEventId = \"{}\"", eventOfCurrentUserByEventId);
        return eventOfCurrentUserByEventId;
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    @Validated({Marker.OnUpdate.class})
    public EventResponseDto updateEvent(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId,
            @Valid @RequestBody @NotNull UpdateEventUserRequest updateEvent
    ) {
        log.info("Изменить событие пользователя userId={}, eventId={}, updateEvent={}",
                userId, eventId, updateEvent);
        EventResponseDto updatedEventOfUser = eventService.updateEventOfUser(userId, eventId, updateEvent);
        log.info("Изменено событие пользователя userId={}, eventId={}, updatedEventOfUser={}",
                userId, eventId, updatedEventOfUser);
        return updatedEventOfUser;
    }
}

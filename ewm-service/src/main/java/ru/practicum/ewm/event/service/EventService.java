package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventFullDto> getEvents(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            String rangeStart,
            String rangeEnd,
            Integer size,
            Integer from);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventRequest);

    List<EventShortDto> getPublicEvents(
            HttpServletRequest request, String text,
            List<Long> categories,
            Boolean paid,
            String rangeStart,
            String rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer size,
            Integer from);

    EventFullDto getPublicEventById(HttpServletRequest request, Long eventId);

    List<EventShortDto> getEventsOfUser(Long userId, Integer size, Integer from);

    EventFullDto createEvent(Long userId, NewEventDto newEvent);

    EventFullDto getEventOfCurrentUserByEventId(Long userId, Long eventId);

    EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

}

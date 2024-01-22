package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<EventResponseDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                     String rangeStart, String rangeEnd, Integer size, Integer from);

    EventResponseDto updateEvent(Long eventId, UpdateEventAdminRequest updateEventRequest);

    List<EventGetPublicResponse> getPublicEvents(HttpServletRequest request, String text, List<Long> categories,
                                                 Boolean paid, String rangeStart, String rangeEnd, Boolean onlyAvailable, String sort,
                                                 Integer size, Integer from);

    EventResponseDto getPublicEventById(HttpServletRequest request, Long eventId);

    List<EventGetPublicResponse> getEventsOfUser(Long userId, Integer size, Integer from);

    EventResponseDto createEvent(Long userId, NewEventDto newEvent);

    EventResponseDto getEventOfCurrentUserByEventId(Long userId, Long eventId);

    EventResponseDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent);

}

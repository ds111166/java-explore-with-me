package ru.practicum.ewm.event.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.data.StateEvent;
import ru.practicum.ewm.event.dto.EventGetPublicResponse;
import ru.practicum.ewm.event.dto.EventResponseDto;
import ru.practicum.ewm.event.dto.Location;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserGetPublicResponse;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class EventMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public EventGetPublicResponse toEventGetPublicResponse(Event event, CategoryDto categoryDto,
                                                           UserGetPublicResponse userShortDto) {
        return EventGetPublicResponse.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(userShortDto)
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    public Event toEvent(NewEventDto newEventDto, Category category, User initiator, LocalDateTime createdDate,
                         LocalDateTime publishedDate, StateEvent state) {
        Boolean paid = newEventDto.getPaid() != null && newEventDto.getPaid();
        Boolean isModeration = newEventDto.getRequestModeration() == null || newEventDto.getRequestModeration();
        Long participantLimit = (newEventDto.getParticipantLimit() == null) ? 0 : newEventDto.getParticipantLimit();
        return Event.builder()
                .category(category)
                .initiator(initiator)
                .lat(newEventDto.getLocation().getLat())
                .lon(newEventDto.getLocation().getLon())
                .eventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter))
                .createdOn(createdDate)
                .publishedOn(publishedDate)
                .paid(paid)
                .requestModeration(isModeration)
                .participantLimit(participantLimit)
                .title(newEventDto.getTitle())
                .annotation(newEventDto.getAnnotation())
                .description(newEventDto.getDescription())
                .state(state)
                .views(0L)
                .confirmedRequests(0L)
                .build();
    }

    public EventResponseDto toEventResponseDto(Event event, CategoryDto categoryDto, UserGetPublicResponse userDto) {
        return EventResponseDto.builder()
                .annotation(event.getAnnotation())
                .category(categoryDto)
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(formatter))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(formatter))
                .id(event.getId())
                .initiator(userDto)
                .location(new Location(event.getLat(), event.getLon()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn((event.getPublishedOn() == null) ? null : event.getPublishedOn().format(formatter))
                .requestModeration(event.getRequestModeration())
                .state(event.getState().name())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }
}

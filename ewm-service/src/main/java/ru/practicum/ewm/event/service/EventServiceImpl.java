package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.data.StateEvent;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.repository.EventRepositoryCustom;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final EventRepository eventRepository;
    private final EventRepositoryCustom eventRepositoryCustom;
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final StatsClient statsClient;

    @Override
    @Transactional
    public EventResponseDto createEvent(Long userId, NewEventDto newEventDto) {
        User initiator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Long categoryId = newEventDto.getCategory();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
        LocalDateTime createdDate = LocalDateTime.now();
        Long differenceInHours = 2L;
        validateEventDate(LocalDateTime.parse(newEventDto.getEventDate(), formatter), createdDate, differenceInHours);
        Event newEvent = eventMapper.toEvent(newEventDto, category, initiator, createdDate,
                null, StateEvent.PENDING);
        try {
            final Event createdEvent = eventRepository.save(newEvent);
            return eventMapper.toEventResponseDto(createdEvent,
                    categoryMapper.toCategoryDto(createdEvent.getCategory()),
                    userMapper.toUserGetPublicResponse(createdEvent.getInitiator())
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public List<EventResponseDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                            String rangeStart, String rangeEnd, Integer size, Integer from) {
        FindEventsParameters parameters = FindEventsParameters.builder()
                .users(users)
                .states(new ArrayList<>())
                .categories(categories)
                .build();
        if (rangeStart != null && rangeEnd != null) {
            parameters.startDate = LocalDateTime.parse(rangeStart, formatter);
            parameters.endDate = LocalDateTime.parse(rangeEnd, formatter);
        }
        final Sort sorting = Sort.by(Sort.Order.desc("createdOn"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parameters, pageable);
        return events.stream()
                .map(event -> eventMapper.toEventResponseDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserGetPublicResponse(event.getInitiator())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponseDto updateEvent(Long eventId, UpdateEventAdminRequest updateEvent) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        //Дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        LocalDateTime currentDateTime = LocalDateTime.now();
        Long differenceInHours = 1L;
        validateEventDate(event.getEventDate(), currentDateTime, differenceInHours);

        String stateAction = updateEvent.getStateAction();
        StateEvent stateEvent = event.getState();

        if (stateAction != null) {
            if (!StateEvent.PENDING.equals(stateEvent)) {
                throw new ConflictException("The event is in the wrong state: " +
                        stateEvent);
            }
            if ("PUBLISH_EVENT".equalsIgnoreCase(stateAction)) {
                event.setState(StateEvent.PUBLISHED);
                event.setPublishedOn(LocalDateTime.now());
            } else if ("REJECT_EVENT".equalsIgnoreCase(stateAction)) {
                event.setState(StateEvent.CANCELED);
                event.setPublishedOn(null);
            } else {
                throw new ConflictException("Invalid stateAction=" + stateAction);
            }
        }


        String annotation = updateEvent.getAnnotation();
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        Long categoryId = updateEvent.getCategory();
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }
        String description = updateEvent.getDescription();
        if (description != null) {
            event.setDescription(description);
        }
        String eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, formatter));
        }
        Location location = updateEvent.getLocation();
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        Boolean paid = updateEvent.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }
        Long participantLimit = updateEvent.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        final String title = updateEvent.getTitle();
        if (title != null) {
            event.setTitle(title);
        }

        final Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventResponseDto(
                savedEvent,
                categoryMapper.toCategoryDto(savedEvent.getCategory()),
                userMapper.toUserGetPublicResponse(savedEvent.getInitiator())
        );
    }

    @Override
    @Transactional
    public List<EventGetPublicResponse> getPublicEvents(HttpServletRequest request, String text, List<Long> categories,
                                                        Boolean paid, String rangeStart, String rangeEnd,
                                                        Boolean onlyAvailable, String sort, Integer size, Integer from) {

        final FindEventsParameters parameters = FindEventsParameters.builder()
                .states(List.of(StateEvent.PUBLISHED))
                .categories(categories)
                .text(text)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .build();
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd],
        // то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (rangeStart != null && rangeEnd != null) {
            LocalDateTime dateTimeStart = LocalDateTime.parse(rangeStart, formatter);
            LocalDateTime dateTimeEnd = LocalDateTime.parse(rangeEnd, formatter);
            if (dateTimeEnd.isBefore(dateTimeStart)) {
                throw new ValidationException(
                        "Invalid dates: rangeEnd="
                                + dateTimeEnd.format(formatter) +
                                " before rangeStart="
                                + dateTimeStart.format(formatter));
            }
            parameters.startDate = dateTimeStart;
            parameters.endDate = dateTimeEnd;
        }

        final Sort sorting;
        if ("EVENT_DATE".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Order.desc("eventDate"));
        } else if ("VIEWS".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Order.desc("views"));
        } else {
            sorting = Sort.by(Sort.Order.desc("createdOn"));
        }
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parameters, pageable);
        createHitsEventEndpoint(request);
        return events.stream()
                .map(event -> eventMapper.toEventGetPublicResponse(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserGetPublicResponse(event.getInitiator())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventResponseDto getPublicEventById(HttpServletRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new NotFoundException("Event with id=" + eventId + " was not published");
        }
        final EventResponseDto eventFullDto = eventMapper.toEventResponseDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserGetPublicResponse(event.getInitiator())
        );
        createHitsEventEndpoint(request);
        long views = getNumberOfHits(event.getCreatedOn(), LocalDateTime.now(), request);
        event.setViews(views);
        eventRepository.save(event);
        return eventFullDto;
    }


    @Override
    @Transactional
    public List<EventGetPublicResponse> getEventsOfUser(Long userId, Integer size, Integer from) {
        final FindEventsParameters parameters = FindEventsParameters.builder()
                .users(List.of(userId))
                .build();
        final Sort sorting = Sort.by(Sort.Order.desc("createdOn"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parameters, pageable);
        return events.stream()
                .map(event -> eventMapper.toEventGetPublicResponse(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserGetPublicResponse(event.getInitiator())))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventResponseDto getEventOfCurrentUserByEventId(Long userId, Long eventId) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return eventMapper.toEventResponseDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserGetPublicResponse(event.getInitiator())
        );
    }

    @Override
    @Transactional
    public EventResponseDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new ConflictException("Event must not be published");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        Long differenceInHours = 2L;
        validateEventDate(event.getEventDate(), currentDateTime, differenceInHours);

        String stateAction = updateEvent.getStateAction();
        if ("SEND_TO_REVIEW".equalsIgnoreCase(stateAction)) {
            event.setState(StateEvent.PENDING);
        } else if ("CANCEL_REVIEW".equalsIgnoreCase(stateAction)) {
            event.setState(StateEvent.CANCELED);
        }

        final String annotation = updateEvent.getAnnotation();
        if (annotation != null) {
            event.setAnnotation(annotation);
        }
        final Long categoryId = updateEvent.getCategory();
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " was not found"));
            event.setCategory(category);
        }
        final String description = updateEvent.getDescription();
        if (description != null) {
            event.setDescription(description);
        }
        final String eventDate = updateEvent.getEventDate();
        if (eventDate != null) {
            event.setEventDate(LocalDateTime.parse(eventDate, formatter));
        }
        final Location location = updateEvent.getLocation();
        if (location != null) {
            event.setLat(location.getLat());
            event.setLon(location.getLon());
        }
        final Boolean paid = updateEvent.getPaid();
        if (paid != null) {
            event.setPaid(paid);
        }
        final Long participantLimit = updateEvent.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        final Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        final String title = updateEvent.getTitle();
        if (title != null) {
            event.setTitle(title);
        }
        final Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventResponseDto(
                savedEvent,
                categoryMapper.toCategoryDto(savedEvent.getCategory()),
                userMapper.toUserGetPublicResponse(savedEvent.getInitiator())
        );
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime now, Long differenceInHours) {
        if (eventDate.isBefore(now.plusHours(differenceInHours))) {
            throw new ValidationException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + eventDate.format(formatter));
        }
    }

    private void createHitsEventEndpoint(HttpServletRequest request) {
        final EndpointHitDto hitEvent = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .app("ewm-service")
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.now().format(formatter))
                .build();
        statsClient.createEndpointHit(hitEvent);
    }

    private long getNumberOfHits(LocalDateTime startDate, LocalDateTime endDate, HttpServletRequest request) {
        Boolean unique = true;
        List<ViewStatsDto> results = statsClient.getStat(
                startDate.format(formatter),
                endDate.format(formatter),
                List.of(request.getRequestURI()),
                unique);
        return (results == null) ? 0 : results.size();
    }
}

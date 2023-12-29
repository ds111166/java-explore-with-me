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
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.stats.client.StatsClient;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
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
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
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
            return eventMapper.toEventFullDto(createdEvent,
                    categoryMapper.toCategoryDto(createdEvent.getCategory()),
                    userMapper.toUserShorDto(createdEvent.getInitiator())
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public List<EventFullDto> getEvents(List<Long> users, List<String> states, List<Long> categories,
                                        String rangeStart, String rangeEnd, Integer size, Integer from) {
        FindEventsParametrs parametrs = FindEventsParametrs.builder()
                .users(users)
                .states(new ArrayList<>())
                .categories(categories)
                .build();
        if (rangeStart != null && rangeEnd != null) {
            parametrs.startDate = LocalDateTime.parse(rangeStart, formatter);
            parametrs.endDate = LocalDateTime.parse(rangeEnd, formatter);
        }
        final Sort sorting = Sort.by(Sort.Order.desc("createdOn"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parametrs, pageable);
        return events.stream()
                .map(event -> eventMapper.toEventFullDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserShorDto(event.getInitiator())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest updateEvent) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        //дата начала изменяемого события должна быть не ранее чем за час от даты публикации. (Ожидается код ошибки 409)
        LocalDateTime currentDateTime = LocalDateTime.now();
        Long differenceInHours = 1L;
        validateEventDate(event.getEventDate(), currentDateTime, differenceInHours);
        String stateAction = updateEvent.getStateAction();
        if (!"PUBLISH_EVENT".equalsIgnoreCase(stateAction) && !"REJECT_EVENT".equalsIgnoreCase(stateAction)) {
            throw new ForbiddenException("Invalid stateAction=" + stateAction);
        }
        StateEvent stateEvent = event.getState();
        if ("PUBLISH_EVENT".equalsIgnoreCase(stateAction) && !stateEvent.equals(StateEvent.PENDING)) {
            throw new ForbiddenException("Cannot publish the event because it's not in the right state: " +
                    stateEvent.name());
        }
        if ("REJECT_EVENT".equalsIgnoreCase(stateAction) && !stateEvent.equals(StateEvent.PENDING)) {
            throw new ForbiddenException("Cannot reject the event because it's not in the right state: " +
                    stateEvent.name());
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
        Integer participantLimit = updateEvent.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        String title = updateEvent.getTitle();
        if (title != null) {
            event.setTitle(title);
        }
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventFullDto(
                savedEvent,
                categoryMapper.toCategoryDto(savedEvent.getCategory()),
                userMapper.toUserShorDto(savedEvent.getInitiator())
        );
    }

    @Override
    @Transactional
    public List<EventShortDto> getPublicEvents(HttpServletRequest request, String text, List<Long> categories,
                                               Boolean paid, String rangeStart, String rangeEnd,
                                               Boolean onlyAvailable, String sort, Integer size, Integer from) {

        FindEventsParametrs parametrs = FindEventsParametrs.builder()
                .states(List.of(StateEvent.PUBLISHED))
                .categories(categories)
                .text(text)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .build();
        //если в запросе не указан диапазон дат [rangeStart-rangeEnd],
        // то нужно выгружать события, которые произойдут позже текущей даты и времени
        if (rangeStart != null && rangeEnd != null) {
            parametrs.startDate = LocalDateTime.parse(rangeStart, formatter);
            parametrs.endDate = LocalDateTime.parse(rangeEnd, formatter);
        } else {
            parametrs.startDate = LocalDateTime.now();
            parametrs.endDate = LocalDateTime.MAX;
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
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parametrs, pageable);
        createHitsEventEndpoint(request);
        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserShorDto(event.getInitiator())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventFullDto getPublicEventById(HttpServletRequest request, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() != StateEvent.PUBLISHED) {
            throw new ForbiddenException("Event with id=" + eventId + " was not published");
        }
        long views = (event.getViews() == null) ? 0 : event.getViews();
        EventFullDto eventFullDto = eventMapper.toEventFullDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserShorDto(event.getInitiator())
        );
        createHitsEventEndpoint(request);
        event.setViews(views + 1);
        eventRepository.save(event);
        return eventFullDto;
    }


    @Override
    @Transactional
    public List<EventShortDto> getEventsOfUser(Long userId, Integer size, Integer from) {
        FindEventsParametrs parametrs = FindEventsParametrs.builder()
                .users(List.of(userId))
                .build();
        final Sort sorting = Sort.by(Sort.Order.desc("createdOn"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final Page<Event> events = eventRepositoryCustom.findEventsByParameters(parametrs, pageable);
        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserShorDto(event.getInitiator())))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public EventFullDto getEventOfCurrentUserByEventId(Long userId, Long eventId) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        return eventMapper.toEventFullDto(
                event,
                categoryMapper.toCategoryDto(event.getCategory()),
                userMapper.toUserShorDto(event.getInitiator())
        );
    }

    @Override
    @Transactional
    public EventFullDto updateEventOfUser(Long userId, Long eventId, UpdateEventUserRequest updateEvent) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        if (event.getState() == StateEvent.PUBLISHED) {
            throw new ValidationException("Event must not be published");
        }
        LocalDateTime currentDateTime = LocalDateTime.now();
        Long differenceInHours = 2L;
        validateEventDate(event.getEventDate(), currentDateTime, differenceInHours);

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
        Integer participantLimit = updateEvent.getParticipantLimit();
        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }
        Boolean requestModeration = updateEvent.getRequestModeration();
        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }
        String title = updateEvent.getTitle();
        if (title != null) {
            event.setTitle(title);
        }
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toEventFullDto(
                savedEvent,
                categoryMapper.toCategoryDto(savedEvent.getCategory()),
                userMapper.toUserShorDto(savedEvent.getInitiator())
        );
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime now, Long differenceInHours) {
        if (eventDate.isBefore(now.plusHours(differenceInHours))) {
            throw new ForbiddenException("Field: eventDate. Error: должно содержать дату, " +
                    "которая еще не наступила. Value: " + eventDate.format(formatter));
        }
    }

    private void createHitsEventEndpoint(HttpServletRequest request) {
        EndpointHitDto hitEvent = EndpointHitDto.builder()
                .ip(request.getRemoteAddr())
                .app("ewm-service")
                .uri(request.getRequestURI())
                .build();
        statsClient.createEndpointHit(hitEvent);
    }
}
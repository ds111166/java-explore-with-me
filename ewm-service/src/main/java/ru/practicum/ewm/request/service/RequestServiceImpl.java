package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.data.StateEvent;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.data.StatusRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;

    @Override
    @Transactional
    public List<ParticipationRequestDto> getRequestsByRequesterId(Long requesterId) {
        userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User with id=" + requesterId + " was not found"));
        List<Request> requests = requestRepository.findByRequesterId(requesterId);
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ParticipationRequestDto createRequest(Long requesterId, Long eventId) {
        final User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException("User with id=" + requesterId + " was not found"));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        // нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getPublishedOn() == null || event.getState() != StateEvent.PUBLISHED) {
            throw new ConflictException("Event with id=" + eventId + " was not published");
        }
        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (Objects.equals(event.getInitiator().getId(), requesterId)) {
            throw new ConflictException("The initiator of the event cannot add a request to participate in his event");
        }
        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.existsByRequesterIdAndEventId(requesterId, eventId)) {
            throw new ConflictException("Cannot add a repeat request");
        }
        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        Long numberConfirmedRequest = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED);
        Integer participantLimit = event.getParticipantLimit();
        if (participantLimit > 0 && !(numberConfirmedRequest < participantLimit)) {
            throw new ConflictException("The event has reached the limit of participation requests");
        }
        /*
            если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в
             состояние подтвержденного
         */
        final Request request = requestMapper.toRequest(
                event,
                requester,
                (event.getRequestModeration()) ? StatusRequest.PENDING : StatusRequest.CONFIRMED
        );
        try {
            final Request createdRequest = requestRepository.save(request);
            return requestMapper.toParticipationRequestDto(createdRequest);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        final Request request = requestRepository.findByIdAndRequesterId(requestId, userId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " was not found"));
        request.setStatus(StatusRequest.CANCELED);
        final Request canceleddRequest = requestRepository.save(request);
        return requestMapper.toParticipationRequestDto(canceleddRequest);
    }

    @Override
    @Transactional
    public List<ParticipationRequestDto> getRequestsFromUserToPartInEvent(Long userId, Long eventId) {

        if (!eventRepository.existsByIdAndInitiatorId(eventId, userId)) {
            throw new NotFoundException("Event with id=" + eventId + " from initiator with id=" +
                    userId + " not found");
        }
        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final List<Request> requests = requestRepository.findAllByEventId(eventId, sorting);
        return requests.stream()
                .map(requestMapper::toParticipationRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult changingStatusRequests(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest updateRequest) {
        final Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        final List<Request> requests = requestRepository.findAllIdInAndEvenId(updateRequest.getRequestIds(), eventId);
        if (updateRequest.getRequestIds().size() != requests.size()) {
            throw new ValidationException("Not all requests for a given event");
        }
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        for (Request request : requests) {
            if (request.getStatus() != StatusRequest.PENDING) {
                throw new ValidationException("Request must have status PENDING");
            }
        }
        final String status = updateRequest.getStatus();
        switch (status) {
            case "CONFIRMED":
                return confirmedRequestStatus(event, requests);
            case "REJECTED":
                return rejectedRequestStatus(event, requests);
            default:
                throw new ValidationException("{Update Request Status.invalid}");
        }
    }

    private EventRequestStatusUpdateResult rejectedRequestStatus(Event event, List<Request> requests) {
        final EventRequestStatusUpdateResult updateResult = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        for (Request request : requests) {
            request.setStatus(StatusRequest.REJECTED);
            ParticipationRequestDto updatedRequestDto = requestMapper.toParticipationRequestDto(request);
            updateResult.getRejectedRequests().add(updatedRequestDto);
        }
        requestRepository.saveAll(requests);
        Long numberConfirmedRequest = requestRepository.countByEventIdAndStatus(
                event.getId(),
                StatusRequest.CONFIRMED);
        event.setConfirmedRequests(numberConfirmedRequest);
        eventRepository.save(event);
        return updateResult;
    }

    private EventRequestStatusUpdateResult confirmedRequestStatus(Event event, List<Request> requests) {
        final Integer participantLimit = event.getParticipantLimit();

        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        Long numberConfirmedRequest = event.getConfirmedRequests();
        if (participantLimit > 0 && !(numberConfirmedRequest < participantLimit)) {
            throw new ConflictException("The event has reached the limit of participation requests");
        }

        final EventRequestStatusUpdateResult updateResult = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(new ArrayList<>())
                .rejectedRequests(new ArrayList<>())
                .build();
        //если для события лимит заявок равен 0 или отключена пре-модерация заявок,
        // то подтверждение заявок не требуется
        if (participantLimit == 0 || !event.getRequestModeration()) {
            return updateResult;
        }
        for (Request request : requests) {
            //если при подтверждении данной заявки, лимит заявок для события исчерпан,
            // то все неподтверждённые заявки необходимо отклонить
            if (numberConfirmedRequest < participantLimit) {
                request.setStatus(StatusRequest.CONFIRMED);
                ParticipationRequestDto updatedRequestDto = requestMapper.toParticipationRequestDto(request);
                updateResult.getConfirmedRequests().add(updatedRequestDto);
                numberConfirmedRequest++;
            } else {
                request.setStatus(StatusRequest.REJECTED);
                ParticipationRequestDto updatedRequestDto = requestMapper.toParticipationRequestDto(request);
                updateResult.getRejectedRequests().add(updatedRequestDto);
            }
        }

        requestRepository.saveAll(requests);
        numberConfirmedRequest = requestRepository.countByEventIdAndStatus(
                event.getId(),
                StatusRequest.CONFIRMED);
        event.setConfirmedRequests(numberConfirmedRequest);
        eventRepository.save(event);
        return updateResult;
    }
}

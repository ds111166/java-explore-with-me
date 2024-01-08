package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;
import ru.practicum.ewm.validation.UpdateRequestStatusValidate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events/{eventId}/requests")
public class PrivateEventRequestController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsFromUserToPartInEvent(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId
    ) {
        log.info("Получить запросы на участие пользователя userId={} в событии eventId={}", userId, eventId);
        List<ParticipationRequestDto> requestsFromUserToPartInEvent =
                requestService.getRequestsFromUserToPartInEvent(userId, eventId);
        log.info("Return requestsFromUserToPartInEvent = \"{}\"", requestsFromUserToPartInEvent);
        return requestsFromUserToPartInEvent;
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusUpdateResult changingStatusRequests(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long eventId,
            @Valid @RequestBody @UpdateRequestStatusValidate EventRequestStatusUpdateRequest updateRequest
    ) {
        log.info("Изменить запросы на участие в событии пользователя userId={}, eventId={}, updateRequest={}",
                userId, eventId, updateRequest);
        EventRequestStatusUpdateResult result = requestService.changingStatusRequests(userId, eventId, updateRequest);
        log.info("Return StatusUpdateResult = \"{}\"", result);
        return result;
    }
}

package ru.practicum.ewm.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.ParticipationRequestDto;
import ru.practicum.ewm.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/requests")
public class PrivateRequestController {
    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByUserId(
            @PathVariable @NotNull Long userId
    ) {
        log.info("Получение запросов от пользователя userId={}", userId);
        List<ParticipationRequestDto> requests = requestService.getRequestsByRequesterId(userId);
        log.info("Return requests = \"{}\"", requests);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createRequest(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Long eventId
    ) {
        log.info("Добавление запроса от пользователя userId={} на участие в событии eventId={}", userId, eventId);
        ParticipationRequestDto createdRequest = requestService.createRequest(userId, eventId);
        log.info("Добавлен запрос = \"{}\"", createdRequest);
        return createdRequest;
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long requestId
    ) {
        log.info("Отмена запроса requestId={} от пользователя userId={}", requestId, userId);
        ParticipationRequestDto canceledRequest = requestService.cancelRequest(userId, requestId);
        log.info("Отменён запрос = \"{}\"", canceledRequest);
        return canceledRequest;
    }
}

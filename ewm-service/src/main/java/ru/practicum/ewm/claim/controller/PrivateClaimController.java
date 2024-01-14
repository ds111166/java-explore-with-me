package ru.practicum.ewm.claim.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.claim.dto.ClaimResponseDto;
import ru.practicum.ewm.claim.service.ClaimService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotNull;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/claims")
public class PrivateClaimController {
    private final ClaimService claimService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public void createClaim(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Long commentId,
            @RequestParam String causeClaim) {
        log.info("Претензия: userId={}, commentId={}, causeClaim={}",
                userId, commentId, causeClaim);
        ClaimResponseDto claim = claimService.createClaim(userId, commentId, causeClaim);
        log.info("Добавлеа претензия: {}", claim);
    }

}

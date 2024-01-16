package ru.practicum.ewm.claim.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.claim.dto.ClaimResponseDto;
import ru.practicum.ewm.claim.service.ClaimService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/claims")
public class AdminClaimController {
    private final ClaimService claimService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ClaimResponseDto> getAdminClaims(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> comments,
            @RequestParam(required = false) List<String> causes,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение списка претензий админом: users={}, comments={}, causes={}, " +
                        "rangeStart={}, rangeEnd={}, size={}, from={}",
                users, comments, causes, rangeStart, rangeEnd, size, from);
        final List<ClaimResponseDto> claims = claimService.getAdminClaims(users, comments, causes,
                rangeStart, rangeEnd, size, from);
        log.info("Return claims = \"{}\"", claims);
        return claims;
    }

    @GetMapping("/{claimId}")
    @ResponseStatus(HttpStatus.OK)
    public ClaimResponseDto getAdminClaimById(
            @PathVariable @NotNull Long claimId) {
        log.info("Получение претензии: claimId={}", claimId);
        final ClaimResponseDto claim = claimService.getAdminClaimById(claimId);
        log.info("Return claim = \"{}\"", claim);
        return claim;
    }

    @DeleteMapping("/{claimId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletingAdminClaim(
            @PathVariable @NotNull Long claimId) {
        log.info("Удаление претензии: claimId={}", claimId);
        claimService.deletingAdminClaim(claimId);
        log.info("Удалена претензия: claimId={}", claimId);
    }
}

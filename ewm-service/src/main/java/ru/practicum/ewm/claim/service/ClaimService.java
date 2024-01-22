package ru.practicum.ewm.claim.service;

import ru.practicum.ewm.claim.dto.ClaimResponseDto;

import java.util.List;

public interface ClaimService {
    ClaimResponseDto createClaim(Long userId, Long commentId, String causeClaim);

    List<ClaimResponseDto> getAdminClaims(List<Long> users, List<Long> comments, List<String> causes,
                                          String rangeStart, String rangeEnd, Integer size, Integer from);

    ClaimResponseDto getAdminClaimById(Long claimId);

    void deletingAdminClaim(Long claimId);
}

package ru.practicum.ewm.claim.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.claim.model.Claim;

import java.time.LocalDateTime;
import java.util.List;

public interface ClaimRepositoryCustom {
    List<Claim> findClaimsByParameters(List<Long> userIds, List<Long> commentIds, List<CauseClaim> causes,
                                       LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}

package ru.practicum.ewm.claim.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.claim.model.Claim;

import java.time.LocalDateTime;
import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    boolean existsByAuthor_IdAndComment_Id(Long userId, Long commentId);

    @Query(value = "select * from claims as c " +
            "where (c.comment_id in (:commentIds) or commentIds is null) " +
            "and (c.author_id in (:userIds) or userIds is null) " +
            "and (c.cause_id in (:causes) or :causes is null) " +
            "and ((c.created_on between :startDate and :endDate) or :startDate is null or :endDate is null)",
            nativeQuery = true)
    List<Claim> findClaimsByParameters(
            @Param("userIds") List<Long> userIds,
            @Param("commentIds") List<Long> commentIds,
            @Param("causes") List<CauseClaim> causes,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

}
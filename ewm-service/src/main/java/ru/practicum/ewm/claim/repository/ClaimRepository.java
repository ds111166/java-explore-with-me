package ru.practicum.ewm.claim.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.claim.model.Claim;

public interface ClaimRepository extends JpaRepository<Claim, Long> {

    boolean existsByAuthor_IdAndComment_Id(Long userId, Long commentId);
}
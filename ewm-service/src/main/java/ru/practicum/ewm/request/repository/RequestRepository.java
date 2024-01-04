package ru.practicum.ewm.request.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.data.StatusRequest;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    List<Request> findByRequesterId(Long requesterId);

    Optional<Request> findByIdAndRequesterId(Long requestId, Long userId);

    boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Long countByEventIdAndStatus(Long eventId, StatusRequest statusRequest);

    List<Request> findAllByEventId(Long eventId, Sort sorting);

    @Query(value = "SELECT * FROM requests as r " +
            "where r.id in :requestIds " +
            "and r.event_id = :eventId",
            nativeQuery = true)
    List<Request> findAllByIdInAndEvenId(
            @Param("requestIds") List<Long> requestIds,
            @Param("eventId") Long eventId);

}

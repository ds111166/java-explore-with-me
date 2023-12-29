package ru.practicum.ewm.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.event.model.Event;

import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long> {

    boolean existsByCategoryId(Long catId);

    Set<Event> findByIdIn(Set<Long> ids);

    boolean existsByIdAndInitiatorId(Long eventId, Long userId);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

}

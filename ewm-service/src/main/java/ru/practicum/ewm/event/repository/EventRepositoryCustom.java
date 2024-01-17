package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.event.dto.FindEventsParameters;
import ru.practicum.ewm.event.model.Event;

public interface EventRepositoryCustom {
    Page<Event> findEventsByParameters(FindEventsParameters parameters, Pageable pageable);
}

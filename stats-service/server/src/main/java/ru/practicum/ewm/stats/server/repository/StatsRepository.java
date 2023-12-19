package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.stats.server.model.EndpoinHit;

public interface StatsRepository extends JpaRepository<EndpoinHit, Long> {
}

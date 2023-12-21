package ru.practicum.ewm.stats.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.stats.server.model.EndpoinHit;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import java.time.LocalDateTime;

import java.util.List;

public interface StatsRepository extends JpaRepository<EndpoinHit, Long> {
    @Query("select " +
            "new ru.practicum.ewm.stats.dto.ViewStatsDto(eh.app, eh.uri, count(eh.ip)) " +
            "from EndpoinHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and (eh.uri in :uris or :uris is null) " +
            "group by eh.app, eh.uri " +
            "order by count(eh.ip) desc")
    List<ViewStatsDto> getStats(@Param("start") LocalDateTime start,
                                    @Param("end") LocalDateTime end,
                                    @Param("uris") List<String> uris);
    @Query("select " +
            "new ru.practicum.ewm.stats.dto.ViewStatsDto(eh.app, eh.uri, count(distinct eh.ip)) " +
            "from EndpoinHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and (eh.uri in :uris or :uris is null) " +
            "group by eh.app, eh.uri " +
            "order by count(distinct eh.ip) desc")
    List<ViewStatsDto> getStatsByUniqueIp(@Param("start") LocalDateTime start,
                                              @Param("end") LocalDateTime end,
                                              @Param("uris") List<String> uris);
}

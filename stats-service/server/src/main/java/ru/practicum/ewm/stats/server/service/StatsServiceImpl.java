package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.mapper.StatsMapper;
import ru.practicum.ewm.stats.server.repository.StatsRepository;
import ru.practicum.ewm.stats.server.model.EndpoinHit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        return null;
    }

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpoinHit endpointHit = statsMapper.toEndpoinHit(endpointHitDto);
        statsRepository.save(endpointHit);
    }
}

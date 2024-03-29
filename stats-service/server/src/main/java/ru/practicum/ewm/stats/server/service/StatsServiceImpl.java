package ru.practicum.ewm.stats.server.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.stats.dto.EndpointHitDto;
import ru.practicum.ewm.stats.dto.ViewStatsDto;
import ru.practicum.ewm.stats.server.mapper.StatsMapper;
import ru.practicum.ewm.stats.server.model.EndpoinHit;
import ru.practicum.ewm.stats.server.repository.StatsRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final StatsMapper statsMapper;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    @Transactional
    public List<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {

        LocalDateTime startDateTime = LocalDateTime.parse(start, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(end, formatter);
        if (endDateTime.isBefore(startDateTime)) {
            throw new ValidationException("endDate=" + end + " before startDate=" + start);
        }
        return unique
                ? statsRepository.getStatsByUniqueIp(startDateTime, endDateTime, uris)
                : statsRepository.getStats(startDateTime, endDateTime, uris);
    }

    @Override
    @Transactional
    public void saveHit(EndpointHitDto endpointHitDto) {
        EndpoinHit endpointHit = statsMapper.toEndpoinHit(endpointHitDto);
        statsRepository.saveAndFlush(endpointHit);
    }
}

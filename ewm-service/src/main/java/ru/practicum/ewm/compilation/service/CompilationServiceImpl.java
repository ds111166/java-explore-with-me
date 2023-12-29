package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CompilationDto createCompilation(NewCompilationDto newCompilation) {

        final Set<Event> events = eventRepository.findByIdIn(newCompilation.getEvents());
        final Compilation compilation = compilationMapper.toCompilation(newCompilation, events);
        final Compilation createdCompilation = compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(
                createdCompilation,
                MakeEventShortDtos(createdCompilation.getEvents())
        );
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {

        if (!compilationRepository.existsById(compId)) {
            throw new NotFoundException("Compilation with id=" + compId + " was not found");
        }
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationDto updateCompilationDto) {

        final Compilation updateCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        final Set<Long> updateCompilationEventIds = updateCompilationDto.getEvents();
        if (updateCompilationEventIds != null) {
            final Set<Event> events = eventRepository.findByIdIn(updateCompilationEventIds);
            updateCompilation.setEvents(events);
        }
        final Boolean updatePinned = updateCompilationDto.getPinned();
        if (updatePinned != null) {
            updateCompilation.setPinned(updatePinned);
        }
        final String updateTitle = updateCompilationDto.getTitle();
        if (updateTitle != null) {
            updateCompilation.setTitle(updateTitle);
        }
        try {
            final Compilation updatedCompilation = compilationRepository.save(updateCompilation);
            return compilationMapper.toCompilationDto(
                    updatedCompilation,
                    MakeEventShortDtos(updatedCompilation.getEvents())
            );
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException(ex.getMessage());
        }
    }

    @Override
    @Transactional
    public List<CompilationDto> getCompilations(Boolean pinned, Integer size, Integer from) {

        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        List<Compilation> compilations = compilationRepository.findByPinnedOrPinnedIsNull(pinned, pinned, pageable);
        return compilations.stream()
                .map(compilation -> compilationMapper.toCompilationDto(
                        compilation,
                        MakeEventShortDtos(compilation.getEvents())
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CompilationDto getCompilationById(Long compId) {

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationMapper.toCompilationDto(compilation, MakeEventShortDtos(compilation.getEvents()));
    }

    private Set<EventShortDto> MakeEventShortDtos(Set<Event> events) {

        return events.stream()
                .map(event -> eventMapper.toEventShortDto(
                        event,
                        categoryMapper.toCategoryDto(event.getCategory()),
                        userMapper.toUserShorDto(event.getInitiator()))
                )
                .collect(Collectors.toSet());
    }
}
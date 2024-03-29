package ru.practicum.ewm.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventGetPublicResponse;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;

@Component
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilation, Set<Event> events) {
        Boolean pinned = newCompilation.getPinned() != null && newCompilation.getPinned();
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(pinned)
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, Set<EventGetPublicResponse> eventShortDtos) {
        return CompilationDto.builder()
                .events(eventShortDtos)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}

package ru.practicum.ewm.compilation.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.Set;

@Component
public class CompilationMapper {
    public Compilation toCompilation(NewCompilationDto newCompilation, Set<Event> events) {
        return Compilation.builder()
                .title(newCompilation.getTitle())
                .pinned(newCompilation.getPinned())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation, Set<EventShortDto> eventShortDtos) {
        return CompilationDto.builder()
                .events(eventShortDtos)
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }
}

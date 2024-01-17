package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.data.StateEvent;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class FindEventsParameters {
    public List<Long> users;
    public List<StateEvent> states;
    public List<Long> categories;
    public String text;
    public Boolean paid;
    public LocalDateTime startDate;
    public LocalDateTime endDate;
    public Boolean onlyAvailable;
}

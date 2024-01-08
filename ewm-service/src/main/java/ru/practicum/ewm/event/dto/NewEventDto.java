package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class NewEventDto {
    @NotNull(groups = Marker.OnCreate.class, message = "the field must not be null")
    @Size(min = 20, max = 2000, groups = Marker.OnCreate.class, message = "invalid field size")
    private String annotation;          // Краткое описание события
    @NotNull(groups = Marker.OnCreate.class, message = "must not be null")
    private Long category;              // Краткое описание события
    @NotNull(groups = Marker.OnCreate.class, message = "the field must not be null")
    @Size(min = 20, max = 7000, groups = Marker.OnCreate.class, message = "invalid field size")
    private String description;         // Полное описание события
    @TimestampHitValidate(groups = Marker.OnCreate.class, message = "invalid date")
    private String eventDate;           // Дата и время на которые намечено событие, формат "yyyy-MM-dd HH:mm:ss"
    @NotNull(groups = Marker.OnCreate.class, message = "the field must not be null")
    private Location location;          // Широта и долгота места проведения события
    private Boolean paid;               // Нужно ли оплачивать участие в событии
    private Long participantLimit;      // Ограничение на количество участников. 0 - означает отсутствие ограничения
    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие
    @NotNull(groups = Marker.OnCreate.class, message = "the field must not be null")
    @Size(min = 3, max = 120, groups = Marker.OnCreate.class, message = "invalid field size")
    private String title;               // Заголовок события
}

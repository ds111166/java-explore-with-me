package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@Builder
@AllArgsConstructor
public class NewEventDto {
    @Max(2000)
    @Min(20)
    private String annotation;          // Краткое описание события
    @NotNull
    private Long category;              // Краткое описание события
    @Max(7000)
    @Min(20)
    private String description;         // Полное описание события
    @TimestampHitValidate
    private String eventDate;           // Дата и время на которые намечено событие, формат "yyyy-MM-dd HH:mm:ss"
    @NotNull
    private Location location;          // Широта и долгота места проведения события
    private Boolean paid;               // Нужно ли оплачивать участие в событии
    private Integer participantLimit;   // Ограничение на количество участников. 0 - означает отсутствие ограничения
    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие
    @Max(120)
    @Min(3)
    private String title;               // Заголовок события
}

package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.stats.dto.validation.TimestampHitValidate;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Null;

@Data
@Builder
public class UpdateEventUserRequest {
    @Max(2000)
    @Min(20)
    @Null
    private String annotation;          // Краткое описание события
    @Null
    private Long category;              // Краткое описание события
    @Max(7000)
    @Min(20)
    @Null
    private String description;         // Полное описание события
    @TimestampHitValidate
    @Null
    private String eventDate;           // Дата и время на которые намечено событие, формат "yyyy-MM-dd HH:mm:ss"
    @Null
    private Location location;          // Широта и долгота места проведения события
    @Null
    private Boolean paid;               // Нужно ли оплачивать участие в событии
    @Null
    private Integer participantLimit;   // Ограничение на количество участников. 0 - означает отсутствие ограничения
    @Null
    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие
    @Max(120)
    @Min(3)
    @Null
    private String title;               // Заголовок события
}

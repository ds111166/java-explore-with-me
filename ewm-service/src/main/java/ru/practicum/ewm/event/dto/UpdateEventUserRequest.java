package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.validation.Marker;
import ru.practicum.ewm.validation.UpdateEventTimeStampValidate;

import javax.validation.constraints.Size;

@Data
@Builder
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, groups = Marker.OnUpdate.class, message = "invalid field size")
    private String annotation;          // Краткое описание события
    private Long category;              // Краткое описание события
    @Size(min = 20, max = 7000, groups = Marker.OnUpdate.class, message = "invalid field size")
    private String description;         // Полное описание события
    @UpdateEventTimeStampValidate(groups = Marker.OnUpdate.class, message = "date invalid")
    private String eventDate;           // Дата и время на которые намечено событие, формат "yyyy-MM-dd HH:mm:ss"
    private Location location;          // Широта и долгота места проведения события
    private Boolean paid;               // Нужно ли оплачивать участие в событии
    private Long participantLimit;   // Ограничение на количество участников. 0 - означает отсутствие ограничения
    private Boolean requestModeration;  // Нужна ли пре-модерация заявок на участие
    private String stateAction;         //Изменение состояния события: SEND_TO_REVIEW или CANCEL_REVIEW
    @Size(min = 3, max = 120, groups = Marker.OnUpdate.class, message = "invalid field size")
    private String title;               // Заголовок события
}

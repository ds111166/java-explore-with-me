package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.request.data.StatusRequest;

@Data
@Builder
@AllArgsConstructor
public class ParticipationRequestDto {
    private Long id;                        // уникальный идентификатор заявки
    private String created;                 // дата и время создания заявки
    private Long event;                     // Идентификатор события на участие в котором подана заявка
    private Long requester;                 // идентификатор пользователя, подавшего заявку
    private StatusRequest status;           // статус заявки
}

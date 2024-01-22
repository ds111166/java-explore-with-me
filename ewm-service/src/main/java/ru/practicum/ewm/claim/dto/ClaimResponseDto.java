package ru.practicum.ewm.claim.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.comment.dto.CommentResponseDto;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimResponseDto {
    private Long id;                   // уникальный идентификатор претензии
    private Long authorId;             // Id Автор
    private CommentResponseDto comment;// Комментарий
    private String createdOn;          //Дата и время претензии (формат "yyyy-MM-dd HH:mm:ss")
    private CauseClaim cause;          // Причина претензии
}

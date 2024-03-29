package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.user.dto.UserGetPublicResponse;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private Long eventId;                   // id события
    private UserGetPublicResponse author;   // Автор
    private String createdOn;               // Дата и время создания комментария (формат "yyyy-MM-dd HH:mm:ss")
    private String editedOn;                // Дата и время последнего редактирования комментария или NULL
    private String text;                    // Текст комментария
    private StateComment state;
}

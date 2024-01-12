package ru.practicum.ewm.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class UpdateCommentUserRequest {
    @Size(min = 1, max = 2048, groups = Marker.OnUpdate.class, message = "invalid field size")
    private String text;
}

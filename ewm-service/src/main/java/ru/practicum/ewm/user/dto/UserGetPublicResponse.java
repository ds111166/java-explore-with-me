package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserGetPublicResponse {
    private Long id;        // уникальный идентификатор пользователя
    private String name;    // имя или логин пользователя
}

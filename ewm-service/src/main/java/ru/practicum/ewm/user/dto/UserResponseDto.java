package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long id;        // уникальный идентификатор пользователя
    private String name;    // имя или логин пользователя
    private String email;   // адрес электронной почты
}

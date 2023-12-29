package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotNull(groups = Marker.OnCreate.class, message = "не должно равняться null")
    @Max(groups = Marker.OnCreate.class, value = 250)
    @Min(groups = Marker.OnCreate.class, value = 2)
    private String name;    // имя или логин пользователя
    @NotNull(groups = Marker.OnCreate.class, message = "не должно равняться null")
    @NotBlank(groups = Marker.OnCreate.class, message = "Адрес электронной почты не может быть пустой")
    @Email(groups = Marker.OnCreate.class, message = "Адрес электронной почты не верного формата")
    @Max(groups = Marker.OnCreate.class, value = 254)
    @Min(groups = Marker.OnCreate.class, value = 6)
    private String email;   // адрес электронной почты NewUserRequest
}
package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewUserRequest {
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(groups = Marker.OnCreate.class, max = 250, min = 2)
    private String name;    // имя или логин пользователя
    @Email(groups = Marker.OnCreate.class)
    @NotBlank(groups = Marker.OnCreate.class)
    @Size(groups = Marker.OnCreate.class, max = 254, min = 6)
    private String email;   // адрес электронной почты NewUserRequest
}
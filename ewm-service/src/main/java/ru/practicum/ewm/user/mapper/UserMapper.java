package ru.practicum.ewm.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserGetPublicResponse;
import ru.practicum.ewm.user.dto.UserResponseDto;
import ru.practicum.ewm.user.model.User;

@Component
public class UserMapper {
    public UserResponseDto toUserResponseDto(User user) {

        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserGetPublicResponse toUserGetPublicResponse(User user) {
        return UserGetPublicResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toUser(NewUserRequest userDto) {

        return User.builder()
                .id(null)
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}

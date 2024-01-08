package ru.practicum.ewm.user.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

@Component
public class UserMapper {
    public UserDto toUserDto(User user) {

        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public UserShortDto toUserShorDto(User user) {
        return UserShortDto.builder()
                .id(user.getId())
                .name(user.getName())
                .build();
    }

    public User toUser(UserDto userDto) {

        return User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(userDto.getEmail())
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

package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserResponseDto;

import java.util.List;

public interface UserService {
    List<UserResponseDto> getUsers(List<Long> ids, Integer size, Integer from);

    UserResponseDto createUser(NewUserRequest newUser);

    void deleteUser(Long userId);
}

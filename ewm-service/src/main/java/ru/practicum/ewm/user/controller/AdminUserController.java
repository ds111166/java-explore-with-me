package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserResponseDto;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class AdminUserController {
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponseDto> getUsers(
            @RequestParam(required = false) List<Long> ids,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение инф. о пользователях: ids={}, size={}, from={}", ids, size, from);
        final List<UserResponseDto> users = userService.getUsers(ids, size, from);
        log.info("Return users = \"{}\"", users);
        return users;
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDto createUser(@Valid @RequestBody NewUserRequest newUser) {
        log.info("Добавление нового пользователя: newUser=\"{}\"", newUser);
        final UserResponseDto user = userService.createUser(newUser);
        log.info("Добавлен пользователь: \"{}\"", user);
        return user;
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable @NotNull Long userId) {
        log.info("Удаление пользователя userId={}", userId);
        userService.deleteUser(userId);
        log.info("Удален пользователь userId={}", userId);
    }
}

package ru.practicum.ewm.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/comments")
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    @Validated({Marker.OnCreate.class})
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createUsersComment(
            @PathVariable @NotNull Long userId,
            @RequestParam @NotNull Long eventId,
            @Valid @RequestBody NewCommentRequest newComment) {
        log.info("Добавление нового комментария: userId={}, eventId={}, newComment=\"{}\"",
                userId, eventId, newComment);
        final CommentResponseDto comment = commentService.createComment(userId, eventId, newComment);
        log.info("Добавлен новый комментарий: \"{}\"", comment);
        return comment;
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletingUsersComment(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long commentId) {
        log.info("Удаление комментария: userId={}, commentId={}", userId, commentId);
        commentService.deleteUsersComment(userId, commentId);
        log.info("Удален комментарий: commentId={}", commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getUsersComments(
            @PathVariable @NotNull Long userId,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение инф. о комментариях пользователя: userId={}, events={}, " +
                        "states={}, rangeStart={}, rangeEnd={}, size={}, from={}",
                userId, events, states, rangeStart, rangeEnd, size, from);
        final List<CommentResponseDto> comments = commentService
                .getUsersComments(userId, events, states, rangeStart, rangeEnd, size, from);
        log.info("Return comments = \"{}\"", comments);
        return comments;
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto getUsersCommentById(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long commentId) {
        log.info("Получение комментария: userId={}, commentId={}", userId, commentId);
        final CommentResponseDto comment = commentService.getUsersCommentById(userId, commentId);
        log.info("Return comment = \"{}\"", comment);
        return comment;
    }

    @PatchMapping("/{commentId}")
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateUserComment(
            @PathVariable @NotNull Long userId,
            @PathVariable @NotNull Long commentId,
            @Valid @RequestBody @NotNull UpdateCommentUserRequest updateCommentUserRequest
    ) {
        log.info("Обновление комментария пользователем: userId={}, commentId={}, updateCommentUserRequest={}",
                userId, commentId, updateCommentUserRequest);
        CommentResponseDto updatedComment = commentService
                .updateUserComment(userId, commentId, updateCommentUserRequest);
        log.info("Обновлен комментарий: \"{}\"", updatedComment);
        return updatedComment;
    }
}

package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;
import ru.practicum.ewm.validation.Marker;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAdminComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение комментариев админом: users={}, events={}, states={}, rangeStart={}, rangeEnd={}," +
                        " size={}, from={}",
                users, events, states, rangeStart, rangeEnd, size, from);
        final List<CommentResponseDto> comments = commentService.getAdminComments(users, events, states,
                rangeStart, rangeEnd, size, from);
        log.info("Return comments = \"{}\"", comments);
        return comments;
    }

    @PatchMapping("/{commentId}")
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto commentProcessingAdmin(
            @PathVariable @NotNull Long commentId,
            @RequestParam(value = "action", defaultValue = "PUBLISHED", required = false) String action
    ) {
        log.info("Обработка комментария админом: commentId={}, action={}",
                commentId, action);
        CommentResponseDto processedComment = commentService.commentProcessingAdmin(commentId, action);
        log.info("Обработан комментарий: \"{}\"", processedComment);
        return processedComment;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminComments(@RequestParam List<Long> ids) {
        log.info("Удаление комментариев админом: ids={}", ids);
        commentService.deleteAdminComments(ids);
        log.info("Удалены комментарии ids={}", ids);
    }
}

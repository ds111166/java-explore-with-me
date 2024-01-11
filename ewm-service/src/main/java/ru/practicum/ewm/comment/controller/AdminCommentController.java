package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
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
@RequestMapping(path = "/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getAdminComments(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) String rangeStart,
            @RequestParam(required = false) String rangeEnd,
            @RequestParam(required = false) String sort,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение комментариев админом: users={}, states={}, rangeStart={}, rangeEnd={}," +
                        " sort={}, size={}, from={}",
                users, states, rangeStart, rangeEnd, sort, size, from);
        final List<CommentResponseDto> comments = commentService.getAdminComments(users, states,
                rangeStart, rangeEnd, sort, size, from);
        log.info("Return comments = \"{}\"", comments);
        return comments;
    }

    @PatchMapping("/{commentId}")
    @Validated({Marker.OnUpdate.class})
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateAdminComment(
            @PathVariable @NotNull Long commentId,
            @Valid @RequestBody @NotNull UpdateCommentAdminRequest updateCommentAdminRequest
    ) {
        log.info("Обновление комментария админом: commentId={}, updateCommentAdminRequest={}",
                commentId, updateCommentAdminRequest);
        CommentResponseDto updatedComment = commentService.updateAdminComment(commentId, updateCommentAdminRequest);
        log.info("Обновлен комментарий: \"{}\"", updatedComment);
        return updatedComment;
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAdminComments(@RequestParam List<Long> ids) {
        log.info("Удаление комментариев админом: ids={}", ids);
        commentService.deleteAdminComments(ids);
        log.info("Удалены комментарии ids={}", ids);
    }
}

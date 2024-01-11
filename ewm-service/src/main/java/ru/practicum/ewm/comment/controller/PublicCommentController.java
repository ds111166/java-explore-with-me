package ru.practicum.ewm.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentResponseDto> getPublishedEventComments(
            @PathVariable @NotNull Long eventId,
            @RequestParam(defaultValue = "asc", required = false) String sort,
            @Min(value = 1) @RequestParam(defaultValue = "10", required = false) Integer size,
            @Min(value = 0) @RequestParam(defaultValue = "0", required = false) Integer from
    ) {
        log.info("Получение публичных комментариев события: eventId={}, , sort={}, size={}, from={}",
                eventId, sort, size, from);
        final List<CommentResponseDto> comments = commentService.getPublishedEventComments(eventId, sort, size, from);
        log.info("Return comments ids = `{}`", comments.stream()
                .map(CommentResponseDto::getId).collect(Collectors.toList()));
        return comments;
    }
}

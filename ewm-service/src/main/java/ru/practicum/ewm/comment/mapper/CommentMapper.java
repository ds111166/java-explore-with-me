package ru.practicum.ewm.comment.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CommentMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public CommentResponseDto toCommentResponseDto(Comment comment, UserShortDto author) {
        final LocalDateTime createdOn = comment.getCreatedOn();
        final LocalDateTime publishedOn = comment.getPublishedOn();
        return CommentResponseDto.builder()
                .id(comment.getId())
                .eventId(comment.getEvent().getId())
                .author(author)
                .createdOn((createdOn == null) ? null : createdOn.format(formatter))
                .publishedOn((publishedOn == null) ? null : publishedOn.format(formatter))
                .text(comment.getText())
                .state(comment.getState())
                .build();
    }

    public Comment toComment(NewCommentRequest newCommentDto, User author, Event event) {
        return Comment.builder()
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now())
                .text(newCommentDto.getText())
                .state(StateComment.PENDING)
                .build();
    }
}

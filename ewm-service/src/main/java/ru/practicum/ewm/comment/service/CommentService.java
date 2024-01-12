package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;

import java.util.List;

public interface CommentService {
    List<CommentResponseDto> getAdminComments(List<Long> users, List<Long> events, List<String> states,
                                              String rangeStart, String rangeEnd, Integer size, Integer from);

    List<CommentResponseDto> getPublishedEventComments(Long eventId, Integer size, Integer from);

    List<CommentResponseDto> getUsersComments(Long userId, List<Long> events, List<String> states,
                                              String rangeStart, String rangeEnd, Integer size, Integer from);

    void deleteAdminComments(List<Long> ids);

    CommentResponseDto createComment(Long userId, Long eventId, NewCommentRequest newComment);

    void deleteComment(Long userId, Long commentId);

    CommentResponseDto getUsersCommentById(Long userId, Long commentId);

    CommentResponseDto updateUserComment(Long userId, Long commentId, UpdateCommentUserRequest updateCommentUserRequest);

    CommentResponseDto commentProcessingAdmin(Long commentId, String action);
}

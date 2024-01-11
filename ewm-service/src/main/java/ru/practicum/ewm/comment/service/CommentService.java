package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;

import java.util.List;

public interface CommentService {
    List<CommentResponseDto> getAdminComments(
            List<Long> users, List<String> states, String rangeStart, String rangeEnd,
            String sort, Integer size, Integer from);

    CommentResponseDto updateAdminComment(Long commentId, UpdateCommentAdminRequest updateCommentAdminRequest);

    void deleteAdminComments(List<Long> ids);

    List<CommentResponseDto> getPublishedEventComments(Long eventId, String sort, Integer size, Integer from);

    CommentResponseDto createComment(Long userId, NewCommentRequest newComment);

    void deleteComment(Long commentId);

    List<CommentResponseDto> getUsersComments(Long userId, List<String> states, String rangeStart, String rangeEnd, String sort, Integer size, Integer from);

    CommentResponseDto getUsersCommentById(Long userId, Long commentId);

    CommentResponseDto updateUserComment(Long userId, Long commentId, UpdateCommentUserRequest updateCommentUserRequest);
}

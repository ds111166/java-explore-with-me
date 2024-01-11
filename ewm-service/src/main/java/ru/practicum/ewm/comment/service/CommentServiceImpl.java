package ru.practicum.ewm.comment.service;

import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentAdminRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;

import java.util.List;

public class CommentServiceImpl implements CommentService {
    @Override
    public List<CommentResponseDto> getAdminComments(
            List<Long> users, List<String> states, String rangeStart, String rangeEnd,
            String sort, Integer size, Integer from) {
        return null;
    }

    @Override
    public CommentResponseDto updateAdminComment(Long commentId, UpdateCommentAdminRequest updateCommentAdminRequest) {
        return null;
    }

    @Override
    public void deleteAdminComments(List<Long> ids) {

    }

    @Override
    public List<CommentResponseDto> getPublishedEventComments(Long eventId, String sort, Integer size, Integer from) {
        return null;
    }

    @Override
    public CommentResponseDto createComment(Long userId, NewCommentRequest newComment) {
        return null;
    }

    @Override
    public void deleteComment(Long commentId) {

    }

    @Override
    public List<CommentResponseDto> getUsersComments(Long userId, List<String> states, String rangeStart, String rangeEnd, String sort, Integer size, Integer from) {
        return null;
    }

    @Override
    public CommentResponseDto getUsersCommentById(Long userId, Long commentId) {
        return null;
    }

    @Override
    public CommentResponseDto updateUserComment(Long userId, Long commentId, UpdateCommentUserRequest updateCommentUserRequest) {
        return null;
    }
}

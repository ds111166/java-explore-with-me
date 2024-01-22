package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentRepositoryCustom {
    List<Comment> findCommentsByParameters(
            List<Long> userIds, List<Long> eventIds, List<StateComment> states, LocalDateTime startDate,
            LocalDateTime endDate, Pageable pageable);

}

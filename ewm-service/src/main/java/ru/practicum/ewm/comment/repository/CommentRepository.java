package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.model.Comment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select * from comments as c " +
            "where (c.event_id in (:eventIds) or :eventIds is null) " +
            "and (c.author_id in (:userIds) or :userIds is null) " +
            "and (c.state_id in (:states) or :states is null) " +
            "and (c.created_on between :startDate and :endDate or :startDate is null or :endDate is null)",
            nativeQuery = true)
    List<Comment> findCommentsByParameters(
            @Param("userIds") List<Long> userIds,
            @Param("eventIds") List<Long> eventIds,
            @Param("states") List<Integer> states,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    Optional<Comment> findByIdAndAuthor_Id(Long commentId, Long userId);

    boolean existsByIdAndAuthor_Id(Long commentId, Long userId);

}

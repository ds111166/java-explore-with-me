package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.data.StateEvent;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ForbiddenException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public List<CommentResponseDto> getAdminComments(
            List<Long> users, List<Long> events, List<String> statesComment, String rangeStart,
            String rangeEnd, Integer size, Integer from) {
        List<Long> userIds = (users != null && !users.isEmpty()) ? users : null;
        List<Long> eventIds = (events != null && !events.isEmpty()) ? events : null;
        List<Integer> states = makeStatesComment(statesComment);
        final LocalDateTime startDate;
        if (rangeStart == null) {
            startDate = null;
        } else {
            try {
                startDate = LocalDateTime.parse(rangeStart, formatter);
            } catch (Exception ex) {
                throw new ValidationException("invalid startDate=" + rangeStart + "ex: " + ex.getMessage());
            }
        }
        final LocalDateTime endDate;
        if (rangeEnd == null) {
            endDate = null;
        } else {
            try {
                endDate = LocalDateTime.parse(rangeEnd, formatter);
            } catch (Exception ex) {
                throw new ValidationException("invalid rangeEnd=" + rangeEnd + "ex: " + ex.getMessage());
            }
        }
        if (endDate != null && startDate != null && endDate.isBefore(startDate)) {
            throw new ValidationException(
                    "Invalid dates: rangeEnd="
                            + endDate.format(formatter) +
                            " before rangeStart="
                            + startDate.format(formatter));
        }
        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final List<Comment> comments = commentRepository
                .findCommentsByParameters(userIds, eventIds, states, startDate, endDate, pageable);
        return comments.stream()
                .map(comment -> commentMapper.toCommentResponseDto(
                        comment,
                        userMapper.toUserShorDto(comment.getAuthor())))
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public List<CommentResponseDto> getUsersComments(
            Long userId, List<Long> events, List<String> statesComment, String rangeStart,
            String rangeEnd, Integer size, Integer from) {

        List<Long> userIds = (userId != null) ? List.of(userId) : null;
        List<Long> eventIds = (events != null && !events.isEmpty()) ? events : null;
        List<Integer> states = makeStatesComment(statesComment);
        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final List<Comment> comments = commentRepository
                .findCommentsByParameters(userIds, eventIds, states, null, null, pageable);
        return comments.stream()
                .map(comment -> commentMapper.toCommentResponseDto(
                        comment,
                        userMapper.toUserShorDto(comment.getAuthor())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentResponseDto> getPublishedEventComments(Long eventId, Integer size, Integer from) {
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        final StateEvent eventState = event.getState();
        if (!StateEvent.PUBLISHED.equals(eventState)) {
            throw new ForbiddenException("Comment with id=" + eventId + " has not been published");
        }
        List<Long> eventIds = List.of(eventId);
        List<Integer> states0 = List.of(StateComment.PUBLISHED.ordinal());

        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final List<Comment> comments = commentRepository
                .findCommentsByParameters(null, eventIds, states0, null, null, pageable);
        return comments.stream()
                .map(comment -> commentMapper.toCommentResponseDto(
                        comment,
                        userMapper.toUserShorDto(comment.getAuthor())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteAdminComments(List<Long> ids) {
        commentRepository.deleteAllByIdInBatch(ids);
    }

    @Override
    @Transactional
    public CommentResponseDto createComment(Long userId, Long eventId, NewCommentRequest newCommentDto) {
        final User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        final Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        StateEvent stateEvent = event.getState();
        if (!StateEvent.PUBLISHED.equals(stateEvent)) {
            throw new ValidationException("Invalid stateEvent=" + stateEvent);
        }
        checkingForSpam(userId, eventId);
        Comment comment = commentMapper.toComment(newCommentDto, author, event);
        final Comment newComment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(newComment,
                userMapper.toUserShorDto(newComment.getAuthor()));
    }

    @Override
    @Transactional
    public void deleteAdminComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }
        commentRepository.deleteById(commentId);
    }


    @Override
    @Transactional
    public void deleteUsersComment(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        final Long authorId = comment.getAuthor().getId();
        if (!Objects.equals(userId, authorId)) {
            throw new ForbiddenException("A comment with id=" + commentId + " is not available for deletion");
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public CommentResponseDto getCommentById(Long commentId) {
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        if (!StateComment.PUBLISHED.equals(comment.getState())) {
            throw new ForbiddenException("Comment with id=" + commentId + " has not been published");
        }
        return commentMapper.toCommentResponseDto(comment,
                userMapper.toUserShorDto(comment.getAuthor()));
    }

    @Override
    @Transactional
    public CommentResponseDto getUsersCommentById(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        final StateComment commentState = comment.getState();
        final User author = comment.getAuthor();
        if (!StateComment.PUBLISHED.equals(commentState) && !userId.equals(author.getId())) {
            throw new ForbiddenException("Comment with id=" + commentId + " has not been published");
        }
        return commentMapper.toCommentResponseDto(comment,
                userMapper.toUserShorDto(comment.getAuthor()));
    }

    @Override
    @Transactional
    public CommentResponseDto getAdminCommentById(Long commentId) {
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        return commentMapper.toCommentResponseDto(comment,
                userMapper.toUserShorDto(comment.getAuthor()));
    }


    @Override
    @Transactional
    public CommentResponseDto updateUserComment(Long userId, Long commentId,
                                                UpdateCommentUserRequest updateCommentUserRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));

        final Long authorId = comment.getAuthor().getId();
        if (!Objects.equals(userId, authorId)) {
            throw new ForbiddenException("A comment with id=" + commentId + " is not available for editing");
        }
        final String text = updateCommentUserRequest.getText();
        if (text != null) {
            comment.setText(text);
            comment.setEditedOn(LocalDateTime.now());
            final Comment updatedComment = commentRepository.save(comment);
            return commentMapper.toCommentResponseDto(updatedComment,
                    userMapper.toUserShorDto(updatedComment.getAuthor()));
        }
        return commentMapper.toCommentResponseDto(comment,
                userMapper.toUserShorDto(comment.getAuthor()));
    }

    @Override
    @Transactional
    public CommentResponseDto commentProcessingAdmin(Long commentId, String action) {
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        comment.setState(makeStateComment(action));
        final Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(updatedComment,
                userMapper.toUserShorDto(updatedComment.getAuthor()));
    }


    private void checkingForSpam(Long userId, Long eventId) {
        final Sort sorting = Sort.by(Sort.Order.desc("id"));
        int size = 1;
        final Pageable pageable = PageRequest.of(0, size, sorting);
        final List<Comment> comments = commentRepository
                .findCommentsByParameters(List.of(userId), List.of(eventId),
                        null, null, null, pageable);
        long differenceInMinutes = 1L;
        if (comments != null && !comments.isEmpty()) {
            Comment comment = comments.get(0);
            if (comment.getCreatedOn().isBefore(LocalDateTime.now().plusMinutes(differenceInMinutes))) {
                throw new ValidationException("this comment is spam");
            }
        }
    }

    private StateComment makeStateComment(String state) {
        if ("PUBLISHED".equalsIgnoreCase(state)) {
            return StateComment.PUBLISHED;
        } else if ("PENDING".equalsIgnoreCase(state)) {
            return StateComment.PENDING;
        } else if ("TO_DELETE".equalsIgnoreCase(state)) {
            return StateComment.TO_DELETE;
        } else {
            throw new ValidationException("Unknown state: " + state);
        }
    }

    private List<Integer> makeStatesComment(List<String> statesComment) {
        if (statesComment == null || statesComment.isEmpty()) {
            return null;
        }
        return statesComment.stream()
                .map(this::makeStateComment)
                .map(Enum::ordinal)
                .collect(Collectors.toList());
    }
}

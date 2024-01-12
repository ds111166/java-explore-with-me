package ru.practicum.ewm.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.dto.NewCommentRequest;
import ru.practicum.ewm.comment.dto.UpdateCommentUserRequest;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public List<CommentResponseDto> getAdminComments(
            List<Long> users, List<Long> events, List<String> statesComment, String rangeStart,
            String rangeEnd, Integer size, Integer from) {
        List<Long> userIds = (users != null && !users.isEmpty()) ? users : null;
        List<Long> eventIds = (events != null && !events.isEmpty()) ? events : null;
        List<StateComment> states = makeStatesComment(statesComment);
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
        List<StateComment> states = makeStatesComment(statesComment);
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
    public List<CommentResponseDto> getPublishedEventComments(
            Long eventId, Integer size, Integer from) {
        List<Long> eventIds = (eventId != null) ? List.of(eventId) : null;
        List<StateComment> states = List.of(StateComment.PUBLISHED);

        final Sort sorting = Sort.by(Sort.Order.asc("id"));
        final Pageable pageable = PageRequest.of(from / size, size, sorting);
        final List<Comment> comments = commentRepository
                .findCommentsByParameters(null, eventIds, states, null, null, pageable);

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
        Comment comment = commentMapper.toComment(newCommentDto, author, event);
        final Comment newComment = commentRepository.save(comment);
        return commentMapper.toCommentResponseDto(newComment,
                userMapper.toUserShorDto(newComment.getAuthor()));
    }

    @Override
    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with id=" + commentId + " was not found");
        }
        commentRepository.deleteById(commentId);
    }


    @Override
    @Transactional
    public CommentResponseDto getUsersCommentById(Long userId, Long commentId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " was not found");
        }
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
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        final String text = updateCommentUserRequest.getText();

        if (text != null) {
            comment.setText(text);
            comment.setState(StateComment.PENDING);
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
        final StateComment stateComment = comment.getState();
        if (stateComment != StateComment.PENDING) {
            throw new ConflictException("The comment is in the wrong state: " + stateComment);
        }
        if ("PUBLISHED".equalsIgnoreCase(action)) {
            comment.setState(StateComment.PUBLISHED);
            comment.setPublishedOn(LocalDateTime.now());
        } else if ("CANCELED_INTOLERANCE".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_INTOLERANCE);
        } else if ("CANCELED_RUDENESS".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_RUDENESS);
        } else if ("CANCELED_EXTREMISM".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_EXTREMISM);
        } else if ("CANCELED_SPAM".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_SPAM);
        } else if ("CANCELED_PORNOGRAPHY".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_PORNOGRAPHY);
        } else if ("CANCELED_PROHIBITION".equalsIgnoreCase(action)) {
            comment.setState(StateComment.CANCELED_PROHIBITION);
        } else {
            throw new ValidationException("Unknown state: " + action);
        }
        final Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentResponseDto(updatedComment, userMapper.toUserShorDto(updatedComment.getAuthor()));
    }

    private List<StateComment> makeStatesComment(List<String> statesComment) {
        if (statesComment == null || statesComment.isEmpty()) {
            return null;
        }
        List<StateComment> states = new ArrayList<>();
        for (String state : statesComment) {
            if ("PUBLISHED".equalsIgnoreCase(state)) {
                states.add(StateComment.PUBLISHED);
            } else if ("CANCELED_INTOLERANCE".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_INTOLERANCE);
            } else if ("CANCELED_RUDENESS".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_RUDENESS);
            } else if ("CANCELED_EXTREMISM".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_EXTREMISM);
            } else if ("CANCELED_SPAM".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_SPAM);
            } else if ("CANCELED_PORNOGRAPHY".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_PORNOGRAPHY);
            } else if ("CANCELED_PROHIBITION".equalsIgnoreCase(state)) {
                states.add(StateComment.CANCELED_PROHIBITION);
            } else {
                throw new ValidationException("Unknown state: " + state);
            }
        }
        return states;
    }
}

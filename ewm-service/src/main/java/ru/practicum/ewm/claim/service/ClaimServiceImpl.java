package ru.practicum.ewm.claim.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.claim.dto.ClaimResponseDto;
import ru.practicum.ewm.claim.mapper.ClaimMapper;
import ru.practicum.ewm.claim.model.Claim;
import ru.practicum.ewm.claim.repository.ClaimRepository;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClaimServiceImpl implements ClaimService {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserMapper userMapper;
    private final ClaimMapper claimMapper;

    @Override
    @Transactional
    public ClaimResponseDto createClaim(Long userId, Long commentId, String cause) {

        final CauseClaim causeClaim = makeCauseClaim(cause);
        final User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " was not found"));
        if (claimRepository.existsByAuthor_IdAndComment_Id(userId, commentId)) {
            throw new ConflictException("There is already a claim from a user with id=" +
                    userId + " for a comment with id=" + commentId);
        }
        final Claim newClaim = claimMapper.toClaim(causeClaim, author, comment);
        final Claim savedClaim = claimRepository.save(newClaim);
        return claimMapper.toClaimResponseDto(
                savedClaim,
                commentMapper.toCommentResponseDto(comment,
                        userMapper.toUserShorDto(comment.getAuthor()))
        );
    }

    @Override
    @Transactional
    public List<ClaimResponseDto> getAdminClaims(
            List<Long> users, List<Long> comments,
            List<String> causesClaim, String rangeStart, String rangeEnd, Integer size, Integer from) {
        List<Long> userIds = (users != null && !users.isEmpty()) ? users : null;
        List<Long> commentIds = (comments != null && !comments.isEmpty()) ? comments : null;
        List<CauseClaim> causes = makeCausesClaim(causesClaim);
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
        List<Claim> claims = claimRepository.findClaimsByParameters(userIds, commentIds, causes,
                startDate, endDate, pageable);
        return claims.stream()
                .map(claim -> claimMapper.toClaimResponseDto(claim,
                        commentMapper.toCommentResponseDto(claim.getComment(),
                                userMapper.toUserShorDto(claim.getComment().getAuthor()))))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClaimResponseDto getAdminClaimById(Long claimId) {
        final Claim claim = claimRepository.findById(claimId)
                .orElseThrow(() -> new NotFoundException("Claim with id=" + claimId + " was not found"));
        final Comment comment = claim.getComment();
        return claimMapper.toClaimResponseDto(
                claim,
                commentMapper.toCommentResponseDto(comment,
                        userMapper.toUserShorDto(comment.getAuthor()))
        );
    }

    @Override
    @Transactional
    public void deletingAdminClaim(Long claimId) {
        if (!claimRepository.existsById(claimId)) {
            throw new NotFoundException("Claim with id=" + claimId + " was not found");
        }
        claimRepository.deleteById(claimId);
    }

    private CauseClaim makeCauseClaim(String cause) {
        try {
            return CauseClaim.getEnum(cause);
        } catch (IllegalArgumentException ex) {
            throw new ValidationException("Unknown cause: " + cause);
        }
    }

    private List<CauseClaim> makeCausesClaim(List<String> causesClaim) {
        return causesClaim.stream()
                .map(this::makeCauseClaim)
                .collect(Collectors.toList());
    }

}

package ru.practicum.ewm.claim.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.claim.dto.ClaimResponseDto;
import ru.practicum.ewm.claim.model.Claim;
import ru.practicum.ewm.comment.dto.CommentResponseDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class ClaimMapper {

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Claim toClaim(CauseClaim causeClaim, User author, Comment comment) {
        return Claim.builder()
                .author(author)
                .comment(comment)
                .createdOn(LocalDateTime.now())
                .cause(causeClaim)
                .build();
    }

    public ClaimResponseDto toClaimResponseDto(Claim claim, CommentResponseDto commentResponseDto) {
        LocalDateTime createdOn = claim.getCreatedOn();
        return ClaimResponseDto.builder()
                .id(claim.getId())
                .authorId(claim.getAuthor().getId())
                .comment(commentResponseDto)
                .createdOn(createdOn.format(formatter))
                .cause(claim.getCause())
                .build();
    }

}

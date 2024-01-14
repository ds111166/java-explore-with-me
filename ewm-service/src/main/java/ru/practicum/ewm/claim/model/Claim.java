package ru.practicum.ewm.claim.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.claim.data.CauseClaim;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "claims")
public class Claim {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // уникальный идентификатор претензии
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;                // Автор
    @ManyToOne
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;            // Комментарий
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;    //Дата и время претензии (формат "yyyy-MM-dd HH:mm:ss")
    @Column(name = "cause_id")
    @Enumerated(EnumType.ORDINAL)
    private CauseClaim cause;          // Причина претензии
}

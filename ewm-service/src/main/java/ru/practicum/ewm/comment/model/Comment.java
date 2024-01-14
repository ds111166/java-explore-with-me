package ru.practicum.ewm.comment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.comment.data.StateComment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    // уникальный идентификатор комментария
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;                // Категория события
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private User author;                // Автор
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;    //Дата и время создания комментария (формат "yyyy-MM-dd HH:mm:ss")
    @Column(name = "edited_on")
    private LocalDateTime editedOn;  //Дата и время последненго редактирования комментария или NULL
    @Column(name = "text", nullable = false, length = 2048)
    private String text;                //Текст комментария
    @Column(name = "state_id")
    @Enumerated(EnumType.ORDINAL)
    private StateComment state;         //Состояние жизненного цикла комментария
}

package ru.practicum.ewm.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.data.StateEvent;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // Уникальный идентификатор события
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;              // Категория события
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    private User initiator;                 // Пользователь
    @Column(name = "lat", nullable = false)
    private Float lat;                      // Широта
    @Column(name = "lon", nullable = false)
    private Float lon;                      // Долгота
    @Column(name = "event_date", nullable = false)
    private LocalDateTime eventDate;        // Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss"
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;        // Дата и время создания события (в формате "yyyy-MM-dd HH:mm:ss")
    @Column(name = "published_on")
    private LocalDateTime publishedOn;      //Дата и время публикации события (в формате "yyyy-MM-dd HH:mm:ss") или NULL
    @Column(name = "is_paid", nullable = false)
    private Boolean paid;                   // Нужно ли оплачивать участие
    @Column(name = "is_moderation", nullable = false)
    private Boolean requestModeration;      // Нужна ли пре-модерация заявок на участие
    @Column(name = "participant_limit", nullable = false)
    private Long participantLimit;          // Ограничение на кол-во участников. 0 - означает отсутствие ограничения
    @Column(name = "title", nullable = false, length = 120)
    private String title;                   // Заголовок
    @Column(name = "annotation", nullable = false, length = 2000)
    private String annotation;              // Краткое описание
    @Column(name = "description", nullable = false, length = 7000)
    private String description;             // Полное описание события
    @Column(name = "state_id")
    @Enumerated(EnumType.ORDINAL)
    private StateEvent state;               // Состояние жизненного цикла события
    @Column(name = "confirmed_requests")
    private Long confirmedRequests;         // Количество одобренных заявок на участие в данном событии
    @Column(name = "views")
    private Long views;                     // Количество просмотрев события
}

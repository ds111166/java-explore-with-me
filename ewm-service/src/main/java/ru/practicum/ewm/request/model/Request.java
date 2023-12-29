package ru.practicum.ewm.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.data.StatusRequest;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                        // уникальный идентификатор заявки
    @Column(name = "created", nullable = false)
    private LocalDateTime created;          // дата и время создания заявки
    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;                    //  события на участие в котором подана заявка
    @ManyToOne
    @JoinColumn(name = "requester_id", nullable = false)
    private User requester;                 // пользователь, создавший заявку
    @Enumerated(EnumType.ORDINAL)
    private StatusRequest status;           // статус заявки
}

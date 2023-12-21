package ru.practicum.ewm.stats.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "endpoint_hits")
public class EndpoinHit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;                    //уникальный идентификатор
    @Column(nullable = false)
    private String app;                 //Идентификатор сервиса для которого записывается информация
    @Column(nullable = false, length = 2048)
    private String uri;                 //URI для которого был осуществлен запрос
    @Column(nullable = false, length = 16)
    private String ip;                  //IP-адрес пользователя, осуществившего запрос
    @Column(nullable = false)
    private LocalDateTime timestamp;    //Дата и время, когда был совершен запрос к эндпоинту
}

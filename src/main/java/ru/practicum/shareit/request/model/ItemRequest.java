package ru.practicum.shareit.request.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Таблица запросов для добавления вещей.
 */
@Data
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "description")
    private String description;

    @OneToOne
    @JoinColumn(name = "requestor_id")
    private User request;

    private LocalDateTime created;
}

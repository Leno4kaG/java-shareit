package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.persistence.*;

/**
 * Таблица пользователь
 */
@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "email", unique = true)
    private String email;
}

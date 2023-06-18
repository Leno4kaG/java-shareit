package ru.practicum.shareit.request.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Модель запроса на создание товара/вещи.
 */
@Data
public class ItemRequestDto {

    private long id;

    private String description;

    private LocalDateTime created;

}

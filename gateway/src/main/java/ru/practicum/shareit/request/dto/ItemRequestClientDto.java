package ru.practicum.shareit.request.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * Модель запроса на создание товара/вещи.
 */
@Data
public class ItemRequestClientDto {

    private long id;
    @NotBlank
    private String description;

    private LocalDateTime created;

}

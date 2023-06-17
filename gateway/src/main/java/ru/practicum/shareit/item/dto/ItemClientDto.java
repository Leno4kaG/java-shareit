package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Модель запроса товара/вещи
 */
@Data
public class ItemClientDto {

    private Long id;

    @NotBlank(message = "Название товара не должно быть пустым.")
    private String name;

}

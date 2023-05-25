package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class ItemDto {

    private Long id;
    @NotBlank(message = "Название товара не должно быть пустым.")
    private String name;
    @NotBlank(message = "Описание товара не должно быть пустым.")
    private String description;
    @NotNull(message = "Статус товара доступности является обязательным параметром.")
    private Boolean available;
}

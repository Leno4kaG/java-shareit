package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemWithBooking;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Модель запроса на создание товара/вещи.
 */
@Data
public class ItemRequestDto {

    private long id;
    @NotBlank
    private String description;

    private Long requestorId;

    private LocalDateTime created;

    private final List<ItemWithBooking> items = new ArrayList<>();
}

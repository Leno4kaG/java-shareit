package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.user.dto.UserClientDto;

import java.time.LocalDateTime;

/**
 * Бронирование
 */
@Data
public class BookingClientDto {
    /**
     * Уникальный идентификатор бронирования
     */
    private long id;
    /**
     * Дата и время начала бронирования
     */
    private LocalDateTime start;
    /**
     * Дата и время конца бронирования
     */
    private LocalDateTime end;
    /**
     * Вещь, которую пользователь бронирует
     */
    private ItemClientDto item;
    /**
     * Пользователь, который осуществляет бронирование
     */
    private UserClientDto booker;
    /**
     * Статус бронирования {@link BookingClientStatus}
     */
    private BookingClientStatus status;
}

package ru.practicum.shareit.booking.dto;

/**
 * Состояние бронирования
 */
public enum BookingClientState {
    /**
     * Все типы бронирования
     */
    ALL,
    /**
     * Текущие типы бронирования
     */
    CURRENT,
    /**
     * Завершенные типы бронирования
     */
    PAST,
    /**
     * Будущие типы бронирования
     */
    FUTURE,
    /**
     * Ожидающие подтверждения бронирования
     */
    WAITING,
    /**
     * Отклонённые бронирования
     */
    REJECTED
}

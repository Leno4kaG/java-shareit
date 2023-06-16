package ru.practicum.shareit.booking.dto;

/**
 * Статус бронирования
 */
public enum BookingClientStatus {
    /**
     * Новое бронирование, ожидает одобрения
     */
    WAITING,
    /**
     * Бронирование
     * подтверждено владельцем
     */
    APPROVED,
    /**
     * Бронирование отклонено владельцем
     */
    REJECTED,
    /**
     * Бронирование отменено создателем
     */
    CANCELED;
}

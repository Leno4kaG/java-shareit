package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {

    private Long bookingId;

    public BookingNotFoundException(Long bookingId) {
        this.bookingId = bookingId;
    }

    public BookingNotFoundException(String message) {
        super(message);
    }

    public Long getBookingId() {
        return this.bookingId;
    }
}

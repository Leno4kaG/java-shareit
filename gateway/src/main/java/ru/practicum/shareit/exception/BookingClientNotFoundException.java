package ru.practicum.shareit.exception;

public class BookingClientNotFoundException extends RuntimeException {

    private Long bookingId;

    public BookingClientNotFoundException(Long bookingId) {
        this.bookingId = bookingId;
    }


    public Long getBookingId() {
        return this.bookingId;
    }
}

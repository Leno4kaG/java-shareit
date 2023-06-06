package ru.practicum.shareit.data;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

public class BookingTestData {

    public static Booking getBooking() {
        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(ItemTestData.getItem());
        booking.setBooker(UserTestData.getUser());
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().minusMinutes(5));
        return booking;
    }

    public static BookingDto getBookingDto() {
        BookingDto booking = new BookingDto();
        booking.setId(1);
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(ItemTestData.getItemDto());
        booking.setBooker(UserTestData.getUserDto());
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusMinutes(5));
        return booking;
    }

    public static BookingRequestDto getBookinReqDto() {
        BookingRequestDto requestDto = new BookingRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusMinutes(1));
        requestDto.setEnd(LocalDateTime.now().plusMinutes(5));
        return requestDto;
    }

    public static BookingForItem getBookinForItem() {
        BookingForItem booking = new BookingForItem();
        booking.setId(1L);
        booking.setBookerId(1L);
        return booking;
    }

    public static Booking getBookingForDBtest() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        booking.setItem(ItemTestData.getItem());
        booking.setBooker(UserTestData.getUser());
        booking.setStart(LocalDateTime.now().minusMinutes(10));
        booking.setEnd(LocalDateTime.now().plusMinutes(15));
        return booking;
    }
}

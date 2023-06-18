package ru.practicum.shareit.data;

import ru.practicum.shareit.booking.dto.BookingClientDto;
import ru.practicum.shareit.booking.dto.BookingClientForItem;
import ru.practicum.shareit.booking.dto.BookingClientRequestDto;
import ru.practicum.shareit.booking.dto.BookingClientStatus;

import java.time.LocalDateTime;

public class BookingTestData {


    public static BookingClientDto getBookingDto() {
        BookingClientDto booking = new BookingClientDto();
        booking.setId(1);
        booking.setStatus(BookingClientStatus.WAITING);
        booking.setItem(ItemTestData.getItemClientDto());
        booking.setBooker(UserTestData.getUserClientDto());
        booking.setStart(LocalDateTime.now());
        booking.setEnd(LocalDateTime.now().plusMinutes(5));
        return booking;
    }

    public static BookingClientRequestDto getBookinReqDto() {
        BookingClientRequestDto requestDto = new BookingClientRequestDto();
        requestDto.setItemId(1);
        requestDto.setStart(LocalDateTime.now().plusMinutes(1));
        requestDto.setEnd(LocalDateTime.now().plusMinutes(5));
        return requestDto;
    }

    public static BookingClientForItem getBookinForItem() {
        BookingClientForItem booking = new BookingClientForItem();
        booking.setId(1L);
        booking.setBookerId(1L);
        return booking;
    }

}

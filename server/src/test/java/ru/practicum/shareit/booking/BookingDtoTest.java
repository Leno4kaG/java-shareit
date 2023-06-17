package ru.practicum.shareit.booking;


import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.user.dto.UserClientDto;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingDtoTest {

    @Test
    void getId() {
        BookingDto bookingDto = BookingTestData.getBookingDto();

        assertEquals(1, bookingDto.getId());
    }

    @Test
    void getStart() {
        BookingDto bookingDto = BookingTestData.getBookingDto();

        assertNotNull(bookingDto.getStart());
    }

    @Test
    void getEnd() {
        BookingDto bookingDto = BookingTestData.getBookingDto();

        assertNotNull(bookingDto.getEnd());
    }

    @Test
    void getItem() {
        BookingDto bookingDto = BookingTestData.getBookingDto();

        assertEquals(1, bookingDto.getItem().getId());
    }

    @Test
    void getBooker() {
    }

    @Test
    void getStatus() {
        BookingDto bookingDto = BookingTestData.getBookingDto();

        assertEquals(1, bookingDto.getBooker().getId());
    }

    @Test
    void setId() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setId(2L);
        assertEquals(2L, bookingDto.getId());
    }

    @Test
    void setStart() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStart(LocalDateTime.now().minusMinutes(5));
        assertTrue(bookingDto.getStart().isBefore(LocalDateTime.now()));
    }

    @Test
    void setEnd() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setEnd(LocalDateTime.now().minusMinutes(5));
        assertTrue(bookingDto.getEnd().isBefore(LocalDateTime.now()));
    }

    @Test
    void setItem() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        ItemClientDto itemDto = new ItemClientDto();
        bookingDto.setItem(itemDto);
        assertEquals(itemDto, bookingDto.getItem());
    }

    @Test
    void setBooker() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        UserClientDto userDto = new UserClientDto();
        bookingDto.setBooker(userDto);
        assertEquals(userDto, bookingDto.getBooker());
    }

    @Test
    void setStatus() {
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStatus(BookingStatus.REJECTED);
        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
    }
}
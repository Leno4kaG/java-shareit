package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.MapperTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    BookingMapper bookingMapper = MapperTestData.getBookingMapper();


    @Test
    void toBookingDto() {
        BookingRequestDto booking = BookingTestData.getBookinReqDto();
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());

        BookingDto result = bookingMapper.toBookingDto(booking);

        assertEquals(bookingDto, result);
    }

    @Test
    void toBookingDtoWhenNull() {

        BookingDto result = bookingMapper.toBookingDto(null);

        assertNull(result);
    }

    @Test
    void toDto() {
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserTestData.getUserForMapper());

        BookingDto result = bookingMapper.toDto(booking);

        assertEquals(bookingDto, result);
    }

    @Test
    void toDtoWhenNull() {

        BookingDto result = bookingMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void toBookingForItem() {
        Booking booking = BookingTestData.getBooking();
        BookingForItem bookingForItem = BookingTestData.getBookinForItem();

        BookingForItem result = bookingMapper.toBookingForItem(booking);

        assertEquals(bookingForItem, result);
    }

    @Test
    void toBookingForItemWhenNull() {

        BookingForItem result = bookingMapper.toBookingForItem(null);

        assertNull(result);
    }

    @Test
    void fromDto() {
        Booking booking = BookingTestData.getBooking();
        Item item = booking.getItem();
        item.setRequest(null);
        item.setOwner(null);
        booking.setItem(item);
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserTestData.getUserForMapper());

        Booking result = bookingMapper.fromDto(bookingDto);

        assertEquals(booking, result);
    }

    @Test
    void fromDtoWhenNull() {
        Booking result = bookingMapper.fromDto(null);

        assertNull(result);
    }

    @Test
    void toListDto() {
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        bookingDto.setBooker(UserTestData.getUserForMapper());

        List<BookingDto> bookingDtoList = bookingMapper.toListDto(List.of(booking));

        assertEquals(List.of(bookingDto), bookingDtoList);
    }

    @Test
    void toListDtoWhenNull() {

        List<BookingDto> bookingDtoList = bookingMapper.toListDto(null);

        assertNull(bookingDtoList);
    }
}
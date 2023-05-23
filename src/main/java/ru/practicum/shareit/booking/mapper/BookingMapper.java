package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {
    @Mapping(target = "item", ignore = true)
    @Mapping(target = "booker", ignore = true)
    @Mapping(target = "status", ignore = true)
    BookingDto toBookingDto(BookingRequest bookingRequest);

    BookingDto toDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    BookingForItem toBookingForItem(Booking booking);

    Booking fromDto(BookingDto bookingDto);

    List<BookingDto> toListDto(List<Booking> bookingList);
}

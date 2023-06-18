package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingForItem;
import ru.practicum.shareit.comment.dto.CommentDto;

import java.util.List;

@Data
@EqualsAndHashCode
public class ItemWithBooking {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingForItem lastBooking;

    private BookingForItem nextBooking;

    private List<CommentDto> comments;

    private Long requestId;
}

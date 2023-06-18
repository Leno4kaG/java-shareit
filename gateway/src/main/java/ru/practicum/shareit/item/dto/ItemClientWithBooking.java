package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.booking.dto.BookingClientForItem;
import ru.practicum.shareit.comment.dto.CommentClientDto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@EqualsAndHashCode
public class ItemClientWithBooking {

    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
    @NotNull
    private Boolean available;

    private BookingClientForItem lastBooking;

    private BookingClientForItem nextBooking;

    private List<CommentClientDto> comments;

    private Long requestId;
}

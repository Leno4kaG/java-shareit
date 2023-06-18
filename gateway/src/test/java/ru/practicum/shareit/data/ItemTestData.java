package ru.practicum.shareit.data;

import ru.practicum.shareit.comment.dto.CommentClientDto;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.item.dto.ItemClientWithBooking;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemTestData {


    public static ItemClientDto getItemClientDto() {
        ItemClientDto item = new ItemClientDto();
        item.setId(1L);
        item.setName("test item");
        return item;
    }

    public static ItemDto getItemDto() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setRequestId(ItemRequestTestData.getItemReqDto().getId());
        item.setAvailable(true);
        return item;
    }

    public static ItemClientWithBooking getItemWithBooking() {

        ItemClientWithBooking item = new ItemClientWithBooking();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setComments(List.of(getCommentDto()));
        item.setNextBooking(BookingTestData.getBookinForItem());
        item.setRequestId(ItemRequestTestData.getItemReqDto().getId());
        item.setAvailable(true);
        return item;
    }

    public static CommentClientDto getCommentDto() {
        CommentClientDto commentDto = new CommentClientDto();
        commentDto.setId(1L);
        commentDto.setText("test comment");
        commentDto.setAuthorName("test author");
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }

}

package ru.practicum.shareit.data;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public class ItemTestData {

    public static Item getItem() {
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setRequest(ItemRequestTestData.getItemReq());
        item.setAvailable(true);
        item.setOwner(UserTestData.getOwner());
        return item;
    }

    public static ItemDto getItemDto() {
        ItemDto item = new ItemDto();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setRequestId(ItemRequestTestData.getItemReq().getId());
        item.setAvailable(true);
        return item;
    }

    public static ItemWithBooking getItemWithBooking() {

        ItemWithBooking item = new ItemWithBooking();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setComments(List.of(getCommentDto()));
        item.setNextBooking(BookingTestData.getBookinForItem());
        item.setRequestId(ItemRequestTestData.getItemReq().getId());
        item.setAvailable(true);
        return item;
    }

    public static CommentDto getCommentDto() {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("test comment");
        commentDto.setAuthorName("test author");
        commentDto.setCreated(LocalDateTime.now());
        return commentDto;
    }

    public static Comment getComment() {
        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("test comment");
        comment.setAuthor(UserTestData.getUser());
        comment.setCreated(LocalDateTime.now());
        comment.setItem(getItem());
        return comment;
    }

    public static Item getItemError() {
        Item item = new Item();
        item.setId(1L);
        item.setName("test item");
        item.setDescription("test item description");
        item.setRequest(ItemRequestTestData.getItemReq());
        item.setAvailable(false);
        item.setOwner(UserTestData.getUser());
        return item;
    }

    public static List<Item> getItems() {
        Item item = getItem();
        item.setId(null);
        Item item1 = getItem();
        item.setId(null);
        item1.setName("test item1");
        Item item2 = getItem();
        item.setId(null);
        item1.setName("test item2");
        return List.of(item, item1, item2);
    }
}

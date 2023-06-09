package ru.practicum.shareit.data;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestTestData {

    public static ItemRequest getItemReq() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setRequest(UserTestData.getUser());
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequestDto getItemReqDto() {
        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setId(1);
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static RequestInfoDto getItemReqInfoDto() {
        RequestInfoDto itemRequest = new RequestInfoDto();
        itemRequest.setId(1);
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of(ItemTestData.getItemDto()));
        return itemRequest;
    }

    public static ItemRequestDto getItemReqDtoWithBooking() {
        ItemRequestDto itemRequest = new ItemRequestDto();
        itemRequest.setId(1);
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static ItemRequest getItemReqWithBooking() {

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1);
        itemRequest.setRequest(UserTestData.getUser());
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }
}

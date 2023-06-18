package ru.practicum.shareit.data;

import ru.practicum.shareit.request.dto.ItemRequestClientDto;
import ru.practicum.shareit.request.dto.RequestClientInfoDto;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestTestData {


    public static ItemRequestClientDto getItemReqDto() {
        ItemRequestClientDto itemRequest = new ItemRequestClientDto();
        itemRequest.setId(1);
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequest;
    }

    public static RequestClientInfoDto getItemReqInfoDto() {
        RequestClientInfoDto itemRequest = new RequestClientInfoDto();
        itemRequest.setId(1);
        itemRequest.setDescription("Item request test");
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setItems(List.of(ItemTestData.getItemDto()));
        return itemRequest;
    }

}

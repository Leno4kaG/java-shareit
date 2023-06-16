package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RequestInfoDtoTest {

    @Test
    void setItems() {
        ItemDto itemDto = ItemTestData.getItemDto();
        RequestInfoDto requestInfoDto = new RequestInfoDto();
        requestInfoDto.setItems(List.of(itemDto));

        assertEquals(1, requestInfoDto.getItems().size());
        assertEquals(itemDto, requestInfoDto.getItems().get(0));
    }
}
package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemRequestMapperTest {

    ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    void toDto() {
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        itemRequestDto.setCreated(itemRequest.getCreated());

        ItemRequestDto result = itemRequestMapper.toDto(itemRequest);

        assertEquals(itemRequestDto, result);
    }

    @Test
    void toDtoWhenNull() {

        ItemRequestDto result = itemRequestMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void fromDto() {
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        itemRequest.setRequest(null);
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        itemRequest.setCreated(itemRequestDto.getCreated());

        ItemRequest result = itemRequestMapper.fromDto(itemRequestDto);

        assertEquals(itemRequest, result);
    }

    @Test
    void fromDtoWhenNull() {

        ItemRequest result = itemRequestMapper.fromDto(null);

        assertNull(result);
    }

    @Test
    void toListDto() {
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        itemRequestDto.setCreated(itemRequest.getCreated());

        List<ItemRequestDto> result = itemRequestMapper.toListDto(List.of(itemRequest));

        assertEquals(List.of(itemRequestDto), result);
    }

    @Test
    void toListDtoWhenNull() {

        List<ItemRequestDto> result = itemRequestMapper.toListDto(null);

        assertNull(result);
    }
}
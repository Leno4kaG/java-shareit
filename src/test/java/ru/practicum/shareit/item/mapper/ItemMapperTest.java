package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ItemMapperTest {

    ItemMapper itemMapper = new ItemMapperImpl();

    @Test
    void toDto() {
        Item item = ItemTestData.getItem();
        ItemDto itemDto = ItemTestData.getItemDto();

        ItemDto result = itemMapper.toDto(item);

        assertEquals(itemDto, result);
    }

    @Test
    void toDtoWhenNull() {

        ItemDto result = itemMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void fromDto() {
        Item item = ItemTestData.getItem();
        ItemDto itemDto = ItemTestData.getItemDto();
        item.setOwner(null);
        item.setRequest(null);

        Item result = itemMapper.fromDto(itemDto);

        assertEquals(item, result);
    }

    @Test
    void fromDtoWhenNull() {

        Item result = itemMapper.fromDto(null);

        assertNull(result);
    }

    @Test
    void toItemWithBooking() {
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        itemWithBooking.setNextBooking(null);
        itemWithBooking.setComments(null);

        ItemWithBooking result = itemMapper.toItemWithBooking(item);

        assertEquals(itemWithBooking, result);
    }

    @Test
    void toItemWithBookingWhenNull() {

        ItemWithBooking result = itemMapper.toItemWithBooking(null);

        assertNull(result);
    }

    @Test
    void toListDto() {
        Item item = ItemTestData.getItem();
        ItemDto itemDto = ItemTestData.getItemDto();

        List<ItemDto> list = itemMapper.toListDto(List.of(item));

        assertEquals(List.of(itemDto), list);
    }

    @Test
    void toListDtoWhenNull() {
        List<ItemDto> list = itemMapper.toListDto(null);

        assertNull(list);
    }
}
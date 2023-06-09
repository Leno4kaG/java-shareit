package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemClientDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toDto(Item item);

    ItemClientDto toClientDto(Item item);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "available", ignore = true)
    Item fromClientDto(ItemClientDto itemClientDto);

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemWithBooking toItemWithBooking(Item item);

    List<ItemDto> toListDto(List<Item> itemList);

    List<ItemClientDto> toListClientDto(List<Item> itemList);
}

package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    ItemDto toDto(Item item);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item fromDto(ItemDto itemDto);

    List<ItemDto> toListDto(List<Item> itemList);
}

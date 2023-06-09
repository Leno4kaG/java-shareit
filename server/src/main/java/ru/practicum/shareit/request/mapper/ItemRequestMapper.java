package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {


    ItemRequestDto toDto(ItemRequest itemRequest);

    @Mapping(target = "items", ignore = true)
    @Mapping(target = "requestId", source = "request.id")
    RequestInfoDto toInfoDto(ItemRequest itemRequest);

    @Mapping(target = "request", ignore = true)
    ItemRequest fromDto(ItemRequestDto itemRequestDto);

    List<ItemRequestDto> toListDto(List<ItemRequest> itemRequests);

    List<RequestInfoDto> toListInfoDto(List<ItemRequest> itemRequests);
}

package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestClientInfoDto {

    private long id;

    private String description;

    private LocalDateTime created;

    private Long requestId;

    private List<ItemDto> items = new ArrayList<>();

    public void setItems(List<ItemDto> newItems) {
        items.addAll(newItems);
    }
}

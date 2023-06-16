package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.item.dto.ItemClientDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RequestClientInfoDto {

    private long id;

    private String description;

    private LocalDateTime created;

    private Long requestId;

    private List<ItemClientDto> items = new ArrayList<>();

    public void setItems(List<ItemClientDto> newItems) {
        items.addAll(newItems);
    }
}

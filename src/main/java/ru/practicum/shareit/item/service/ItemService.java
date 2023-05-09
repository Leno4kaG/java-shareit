package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final ItemMapper itemMapper;

    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = itemMapper.fromDto(itemDto);
        User user = userRepository.getUserById(userId);
        item.setOwner(user);
        itemDto.setId(itemRepository.createItem(item).getId());
        return itemDto;
    }

    public ItemDto updateItem(ItemDto itemDto, long userId) {
        User user = userRepository.getUserById(userId);
        Item item = itemRepository.getItem(itemDto.getId(), userId);
        if (user.getId() != userId) {
            throw new ItemNotFoundException(item.getId());
        }
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        itemRepository.updateItem(item);
        itemDto = itemMapper.toDto(item);
        return itemDto;
    }

    public ItemDto getItem(long itemId, long userId) {
        return itemMapper.toDto(itemRepository.getItem(itemId, userId));
    }

    public List<ItemDto> getAllItems(long userId) {
        return itemMapper.toListDto(itemRepository.getAllItems(userId));
    }

    public List<ItemDto> searchItems(String text, long userId) {
        return itemMapper.toListDto(itemRepository.searchItems(text.toLowerCase(Locale.ROOT), userId));
    }
}

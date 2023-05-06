package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    Item createItem(Item item);

    Item updateItem(Item item);

    Item getItem(long itemId, long userId);

    List<Item> getAllItems(long userId);

    List<Item> searchItems(String text, long userId);
}

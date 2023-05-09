package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class ItemRepositoryInMemory implements ItemRepository {

    private Map<Long, Item> itemMap = new HashMap<>();

    private static long id = 1;

    @Override
    public Item createItem(Item item) {
        item.setId(id++);
        itemMap.put(item.getId(), item);
        log.info("Create item by name {} ", item.getName());
        return item;
    }

    @Override
    public Item updateItem(Item item) {
        itemMap.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(long itemId, long userId) {
        Item item = itemMap.get(itemId);
        if (item == null) {
            throw new ItemNotFoundException(itemId);
        }

        return item;
    }

    @Override
    public List<Item> getAllItems(long userId) {
        List<Item> items = new ArrayList<>(itemMap.values());
        List<Item> userItem = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner().getId() == userId) {
                userItem.add(item);
            }
        }
        return userItem;
    }

    @Override
    public List<Item> searchItems(String text, long userId) {
        List<Item> items = new ArrayList<>(itemMap.values());
        List<Item> foundItem = new ArrayList<>();

        for (Item item : items) {
            if (item.isAvailable() && (item.getName().toLowerCase().contains(text)
                    || item.getDescription().toLowerCase().contains(text))) {
                foundItem.add(item);
            }
        }
        return foundItem;
    }
}

package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody ItemDto itemDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItemDto(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        itemDto.setId(itemId);
        return ResponseEntity.ok(itemService.updateItem(itemDto, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@NotNull @RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public List<ItemDto> getAllItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text, @NotNull @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.searchItems(text, userId);
    }

}

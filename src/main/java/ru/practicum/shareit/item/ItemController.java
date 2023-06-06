package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

/**
 * Контроллер для товаров
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ResponseEntity<ItemDto> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @Valid @RequestBody ItemDto itemDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createItem(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemWithBooking> updateItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                         @RequestBody ItemDto itemDto, @PathVariable long itemId) {
        itemDto.setId(itemId);
        return ResponseEntity.ok(itemService.updateItem(itemDto, userId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemWithBooking> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                                   @PathVariable long itemId) {
        return ResponseEntity.ok(itemService.getItem(itemId, userId));
    }

    @GetMapping
    public List<ItemWithBooking> getAllItems(@Validated @RequestParam(name = "from", required = false) Integer from,
                                             @Validated @RequestParam(name = "size", required = false) Integer size,
                                             @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@Validated @RequestParam(name = "from", required = false) Integer from,
                                     @Validated @RequestParam(name = "size", required = false) Integer size,
                                     @RequestParam(name = "text") String text,
                                     @RequestHeader("X-Sharer-User-Id") long userId) {
        if (text == null || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemService.searchItems(text, userId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDto> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @Valid @RequestBody CommentDto commentDto) {
        return ResponseEntity.status(HttpStatus.OK).body(itemService.addComment(userId, itemId, commentDto));
    }

}

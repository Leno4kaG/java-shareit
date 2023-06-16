package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentClientDto;
import ru.practicum.shareit.item.dto.ItemClientDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

/**
 * Контроллер для товаров
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemClientController {

    private final ItemClient itemService;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @Valid @RequestBody ItemClientDto itemClientDto) {

        return itemService.createItem(userId, itemClientDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItemDto(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @RequestBody ItemClientDto itemClientDto, @PathVariable long itemId) {
        itemClientDto.setId(itemId);
        return itemService.updateItemDto(userId, itemClientDto, itemId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long itemId) {
        return itemService.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                              @RequestParam(name = "text") String text,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        if (text == null || text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return itemService.searchItems(from, size, text, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentClientDto commentClientDto) {
        return itemService.addComment(userId, itemId, commentClientDto);
    }

}

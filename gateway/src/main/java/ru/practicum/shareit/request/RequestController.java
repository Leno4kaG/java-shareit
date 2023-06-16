package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestClientDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Контроллер для работы с запросами.
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestClient itemRequestService;

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @Valid @RequestBody ItemRequestClientDto requestDto) {
        return itemRequestService.createRequest(userId, requestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsForOwner(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                 @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}

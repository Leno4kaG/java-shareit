package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;


/**
 * Контроллер для работы с запросами.
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ResponseEntity<ItemRequestDto> createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                       @RequestBody ItemRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemRequestService.createRequest(userId, requestDto));
    }

    @GetMapping
    public List<RequestInfoDto> getAllRequestsForOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.getAllRequestsForOwner(userId);
    }

    @GetMapping("/all")
    public List<RequestInfoDto> getAllRequests(@RequestParam(name = "from", defaultValue = "0") Integer from,
                                                @RequestParam(name = "size", defaultValue = "10") Integer size,
                                               @RequestHeader("X-Sharer-User-Id") Long userId) {

        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<RequestInfoDto> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                         @PathVariable Long requestId) {
        return ResponseEntity.ok(itemRequestService.getItemRequestById(userId, requestId));
    }
}

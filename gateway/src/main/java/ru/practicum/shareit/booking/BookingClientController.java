package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingClientRequestDto;
import ru.practicum.shareit.booking.dto.BookingClientState;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

/**
 * Контроллер бронирования
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingClientController {

    private final BookingClient bookingService;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody BookingClientRequestDto bookingClientRequestDto) {
        log.info("Create booking with user id {},booking client={}", userId, bookingClientRequestDto);
        return bookingService.createBooking(userId, bookingClientRequestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId,
                                                    @RequestParam(name = "approved") Boolean approved) {
        log.info("update booking booking id {}", bookingId);
        return bookingService.updateBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsForCurrentUser(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") BookingClientState state) {
        return bookingService.getBookingsForCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsForAllItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") BookingClientState state) {

        return bookingService.getBookingsForAllItems(userId, state, from, size);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * Контроллер бронирования
 */
@Slf4j
@RestController
@Validated
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody BookingRequestDto bookingRequestDto) {

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingRequestDto));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId,
                                                    @RequestParam(name = "approved") Boolean approved) {
        return ResponseEntity.ok(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getBookingsForCurrentUser(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                      @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                      @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsForCurrentUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForAllItems(@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(name = "size", defaultValue = "10") Integer size,
                                                   @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {

        return bookingService.getBookingsForAllItems(userId, state, from, size);
    }
}

package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Контроллер бронирования
 */
@Slf4j
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDto> createBooking(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @Valid @RequestBody BookingRequest bookingRequest) {

        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(userId, bookingRequest));
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<BookingDto> updateBooking(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                    @PathVariable long bookingId,
                                                    @RequestParam(name = "approved") Boolean approved) {
        return ResponseEntity.ok(bookingService.updateBooking(userId, bookingId, approved));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDto> getBooking(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long bookingId) {
        return ResponseEntity.ok(bookingService.getBooking(userId, bookingId));
    }

    @GetMapping
    public List<BookingDto> getBookingsForCurrentUser(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsForCurrentUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsForAllItems(@NotNull @RequestHeader("X-Sharer-User-Id") long userId,
                                                   @RequestParam(name = "state", defaultValue = "ALL") BookingState state) {
        return bookingService.getBookingsForAllItems(userId, state);
    }
}

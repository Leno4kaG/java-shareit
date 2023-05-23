package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingRequest;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepositoryDB userRepositoryDB;

    private final ItemRepositoryDB itemRepositoryDB;

    private final BookingMapper bookingMapper;

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;

    @Transactional
    public BookingDto createBooking(Long userId, BookingRequest bookingRequest) {
        final long itemId = bookingRequest.getItemId();
        User user = findUser(userId);
        log.info("User {}", user);
        Item item = itemRepositoryDB.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (userId == item.getOwner().getId()) {
            log.error("Owner is not booking item");
            throw new ItemNotFoundException(itemId);
        }
        log.info("Item {}", item);
        if (!item.getAvailable()) {
            log.error("Available = false");
            throw new BookingValidationException();
        }
        validationDate(bookingRequest.getStart(), bookingRequest.getEnd());
        BookingDto bookingDto = bookingMapper.toBookingDto(bookingRequest);
        bookingDto.setStatus(BookingStatus.WAITING);
        bookingDto.setBooker(userMapper.toDto(user));
        bookingDto.setItem(itemMapper.toDto(item));

        Booking booking = bookingMapper.fromDto(bookingDto);
        bookingDto.setId(bookingRepository.save(booking).getId());
        return bookingDto;
    }

    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        findUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        Item item = booking.getItem();
        if (item.getOwner().getId() != userId) {
            log.error("User id != {} owner in item !!!", userId);
            throw new BookingNotFoundException(bookingId);
        }
        if (approved) {
            if (booking.getStatus().equals(BookingStatus.APPROVED)) {
                log.error("Status approved !!!");
                throw new BookingValidationException();
            }
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking);

        return bookingMapper.toDto(booking);
    }

    @Transactional
    public BookingDto getBooking(Long userId, Long bookingId) {
        findUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        Item item = booking.getItem();
        long ownerId = item.getOwner().getId();
        if ((ownerId != userId) && (booking.getBooker().getId() != userId)) {
            log.error("User id != {} owner in item {} or booker id {} ", userId, ownerId, bookingId);
            throw new BookingNotFoundException(bookingId);
        }
        return bookingMapper.toDto(booking);
    }

    @Transactional
    public List<BookingDto> getBookingsForCurrentUser(Long userId, BookingState state) {
        User user = findUser(userId);
        return bookingMapper.toListDto(getBookings(state, user));
    }

    @Transactional
    public List<BookingDto> getBookingsForAllItems(Long userId, BookingState state) {
        User owner = findUser(userId);
        List<Item> itemList = itemRepositoryDB.findAllByOwner(owner);
        List<Booking> bookings = getBookingsByItem(state, itemList);
        return bookingMapper.toListDto(bookings);
    }

    @Transactional
    private List<Booking> getBookings(BookingState state, User booker) {
        Sort sort = Sort.by("start").descending();
        LocalDateTime date = LocalDateTime.now();
        if (BookingState.ALL.equals(state)) {
            return bookingRepository.findAllByBookerId(booker.getId(), sort);
        } else if (BookingState.CURRENT.equals(state)) {
            return bookingRepository.findAllCurrent(booker);
        } else if (BookingState.PAST.equals(state)) {
            return bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), date, sort);
        } else if (BookingState.FUTURE.equals(state)) {
            return bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), date, sort);
        } else if (BookingState.WAITING.equals(state) || BookingState.REJECTED.equals(state)) {
            return bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.valueOf(state.name()), sort);
        } else {
            log.info("Booking with state {} not found", state);
            throw new BookingException(String.format("Unknown state: %s", state));
        }
    }

    @Transactional
    private List<Booking> getBookingsByItem(BookingState state, List<Item> items) {
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        LocalDateTime date = LocalDateTime.now();
        for (Item item : items) {
            if (BookingState.ALL.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemId(item.getId(), sort));
            }
            if (BookingState.CURRENT.equals(state)) {
                bookings.addAll(bookingRepository.findAllCurrentByItem(item));
            }
            if (BookingState.PAST.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemIdAndEndBefore(item.getId(), date, sort));
            }
            if (BookingState.FUTURE.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemIdAndStartAfter(item.getId(), date, sort));
            }
            if (BookingState.WAITING.equals(state) || BookingState.REJECTED.equals(state)) {

                bookings.addAll(bookingRepository.findBookingByItemIdAndStatus(item.getId(), BookingStatus.valueOf(state.name()), sort));
            }
        }
        return bookings;
    }

    private void validationDate(LocalDateTime startDate, LocalDateTime endDate) {
        if (endDate.isBefore(LocalDateTime.now())) {
            log.error("End date in past");
            throw new BookingValidationException();
        }
        if (endDate.isBefore(startDate) || startDate.equals(endDate)) {
            log.error("End date is before start date {}  {}", startDate, endDate);
            throw new BookingValidationException();
        }
        if (startDate.isBefore(LocalDateTime.now())) {
            log.error("Start date in past");
            throw new BookingValidationException();
        }
    }

    @Transactional
    private User findUser(Long userId) {
        return userRepositoryDB.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}

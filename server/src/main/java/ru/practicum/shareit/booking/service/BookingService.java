package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    private final UserRepository userRepository;

    private final ItemRepository itemRepository;

    private final BookingMapper bookingMapper;

    private final UserMapper userMapper;

    private final ItemMapper itemMapper;


    @Transactional
    public BookingDto createBooking(Long userId, BookingRequestDto bookingRequestDto) {
        final long itemId = bookingRequestDto.getItemId();
        User user = findUser(userId);
        log.info("User {}", user);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (userId == item.getOwner().getId()) {
            log.error("Owner is not booking item");
            throw new ItemNotFoundException(itemId);
        }
        log.info("Item {}", item);
        if (!item.getAvailable()) {
            log.error("Available = false");
            throw new BookingValidationException();
        }
        BookingDto bookingClientDto = bookingMapper.toBookingDto(bookingRequestDto);
        bookingClientDto.setStatus(BookingStatus.WAITING);
        bookingClientDto.setBooker(userMapper.toClientDto(user));
        bookingClientDto.setItem(itemMapper.toClientDto(item));

        Booking booking = bookingMapper.fromDto(bookingClientDto);
        bookingClientDto.setId(bookingRepository.save(booking).getId());
        return bookingClientDto;
    }

    @Transactional
    public BookingDto updateBooking(Long userId, Long bookingId, Boolean approved) {
        findUser(userId);
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
        Item item = booking.getItem();
        if (userId != item.getOwner().getId()) {
            log.error("User id != {} owner in item !!!", userId);
            throw new BookingNotFoundException(bookingId);
        }
        if (approved) {
            if (BookingStatus.APPROVED.equals(booking.getStatus())) {
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

    @Transactional(readOnly = true)
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

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForCurrentUser(Long userId, BookingState state, Integer from, Integer size) {
        User user = findUser(userId);
        return bookingMapper.toListDto(getBookings(state, user, from / size, size));
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsForAllItems(Long userId, BookingState state, Integer from, Integer size) {
        User owner = findUser(userId);
        List<Item> itemList = itemRepository.findAllByOwnerId(owner.getId());
        List<Booking> bookings = getBookingsByItem(state, itemList, from / size, size);
        return bookingMapper.toListDto(bookings);
    }


    private List<Booking> getBookings(BookingState state, User booker, Integer from, Integer size) {
        Sort sort = Sort.by("start").descending();
        Pageable sortedByStart = PageRequest.of(from, size, sort);
        LocalDateTime date = LocalDateTime.now();
        if (BookingState.ALL.equals(state)) {
            Pageable sortedByStartAll = PageRequest.of(from, size, sort);
            return bookingRepository.findAllByBookerId(booker.getId(), sortedByStartAll);
        } else if (BookingState.CURRENT.equals(state)) {
            Sort sortId = Sort.by("id").ascending();
            Pageable sortedById = PageRequest.of(from, size, sortId);
            return bookingRepository.findAllCurrent(booker.getId(), sortedById);
        } else if (BookingState.PAST.equals(state)) {
            return bookingRepository.findAllByBookerIdAndEndBefore(booker.getId(), date, sortedByStart);
        } else if (BookingState.FUTURE.equals(state)) {
            return bookingRepository.findAllByBookerIdAndStartAfter(booker.getId(), date, sortedByStart);
        } else {
            return bookingRepository.findAllByBookerIdAndStatus(booker.getId(), BookingStatus.valueOf(state.name()), sortedByStart);
        }
    }

    private List<Booking> getBookingsByItem(BookingState state, List<Item> items, Integer from, Integer size) {
        List<Booking> bookings = new ArrayList<>();
        Sort sort = Sort.by("start").descending();
        Pageable sortedByStart = PageRequest.of(from, size, sort);
        LocalDateTime date = LocalDateTime.now();
        for (Item item : items) {
            if (BookingState.ALL.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemId(item.getId(), sortedByStart));
            }
            if (BookingState.CURRENT.equals(state)) {
                bookings.addAll(bookingRepository.findAllCurrentByItem(item, sortedByStart));
            }
            if (BookingState.PAST.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemIdAndEndBefore(item.getId(), date, sortedByStart));
            }
            if (BookingState.FUTURE.equals(state)) {
                bookings.addAll(bookingRepository.findAllByItemIdAndStartAfter(item.getId(), date, sortedByStart));
            }
            if (BookingState.WAITING.equals(state) || BookingState.REJECTED.equals(state)) {

                bookings.addAll(bookingRepository.findBookingByItemIdAndStatus(item.getId(),
                        BookingStatus.valueOf(state.name()), sortedByStart));
            }
        }
        return bookings;
    }


    private User findUser(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }
}

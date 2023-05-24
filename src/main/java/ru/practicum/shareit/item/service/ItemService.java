package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepositoryDB itemRepository;
    private final UserRepositoryDB userRepository;

    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        if (itemDto.getName().isBlank()) {
            log.error("Наименование товара пустое!!");
            throw new ValidationException();
        }
        Item item = itemMapper.fromDto(itemDto);
        log.info("Item {}", item);
        User owner = getOwner(userId);
        item.setOwner(owner);
        itemDto.setId(itemRepository.save(item).getId());
        return itemDto;
    }

    @Transactional
    public ItemWithBooking updateItem(ItemDto itemDto, long userId) {
        getOwner(userId);
        final long itemId = itemDto.getId();
        Item item = getItem(itemId, userId, itemDto);
        itemRepository.save(item);
        return getItemWithBooking(item, userId);
    }

    @Transactional
    public ItemWithBooking getItem(long itemId, long userId) {
        getOwner(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        return getItemWithBooking(item, userId);
    }

    @Transactional
    public List<ItemWithBooking> getAllItems(long userId) {
        List<Item> items = itemRepository.findAllByOwner(getOwner(userId)).stream()
                .sorted(Comparator.comparing(Item::getId)).collect(Collectors.toList());
        return getItemsWithBooking(items, userId);
    }

    @Transactional
    public List<ItemDto> searchItems(String text, long userId) {
        return itemMapper.toListDto(itemRepository.searchItems(text));
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty() || commentDto.getText().isBlank()) {
            log.error("Коммент не должен быть пустым!!");
            throw new CommentValidationException();
        }
        Sort sort = Sort.by("start").descending();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), sort);
        if (bookings.stream().anyMatch(b -> b.getBooker().equals(user) && b.getEnd().isBefore(LocalDateTime.now()))) {
            commentDto.setAuthorName(user.getName());
            commentDto.setCreated(LocalDateTime.now());
            Comment comment = commentMapper.fromDto(commentDto);
            comment.setAuthor(user);
            comment.setItem(item);
            commentDto.setId(commentRepository.save(comment).getId());
        } else {
            log.error("У пользователя нет прав для добавления комментария!!");
            throw new CommentValidationException();
        }
        return commentDto;
    }

    @Transactional
    private User getOwner(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    @Transactional
    private List<ItemWithBooking> getItemsWithBooking(List<Item> items, Long userId) {
        List<ItemWithBooking> withBookings = new ArrayList<>();
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        for (Item item : items) {
            Booking lastBooking = bookingRepository.findBookingByItemIdAndStatusAndStartBefore(item.getId(),
                            BookingStatus.APPROVED, date, sort)
                    .stream().findFirst().orElse(null);
            Booking nextBooking = bookingRepository.findBookingByItemIdAndStatusAndStartAfter(item.getId(),
                            BookingStatus.APPROVED, date, Sort.by("start").ascending())
                    .stream().findFirst().orElse(null);
            ItemWithBooking itemWithBooking = itemMapper.toItemWithBooking(item);
            log.info("Last booking {}", lastBooking);
            log.info("Next booking {}", nextBooking);
            if (userId.equals(item.getOwner().getId())) {
                itemWithBooking.setNextBooking(bookingMapper.toBookingForItem(nextBooking));
                itemWithBooking.setLastBooking(bookingMapper.toBookingForItem(lastBooking));
            }
            withBookings.add(itemWithBooking);
        }
        return withBookings;
    }

    @Transactional
    private Item getItem(Long itemId, Long userId, ItemDto itemDto) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        if (item.getOwner().getId() != userId) {
            throw new ItemNotFoundException(item.getId());
        }
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean isAvailable = itemDto.getAvailable();
        if (name != null) {
            item.setName(name);
        }
        if (description != null) {
            item.setDescription(description);
        }
        if (isAvailable != null) {
            item.setAvailable(isAvailable);
        }
        return item;
    }

    @Transactional
    private ItemWithBooking getItemWithBooking(Item item, Long userId) {
        List<CommentDto> comments = commentMapper.toListDto(commentRepository.findAllByItem(item));
        List<ItemWithBooking> bookings = getItemsWithBooking(List.of(item), userId);
        if (bookings.isEmpty()) {
            throw new ItemNotFoundException(item.getId());
        }
        ItemWithBooking itemWithBooking = bookings.get(0);
        itemWithBooking.setComments(comments);
        return itemWithBooking;
    }
}

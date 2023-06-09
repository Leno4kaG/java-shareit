package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final ItemRequestRepository itemRequestRepository;


    @Transactional
    public ItemDto createItem(ItemDto itemDto, long userId) {
        Item item = itemMapper.fromDto(itemDto);
        log.info("Item {}", item);
        User owner = getOwner(userId);
        Long itemReqId = itemDto.getRequestId();
        if (itemReqId != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ItemRequestNotFoundException(itemReqId));
            item.setRequest(itemRequest);
            itemDto.setRequestId(itemReqId);
        }
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

    @Transactional(readOnly = true)
    public ItemWithBooking getItem(long itemId, long userId) {
        getOwner(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        return getItemWithBooking(item, userId);
    }

    @Transactional(readOnly = true)
    public List<ItemWithBooking> getAllItems(long userId, Integer from, Integer size) {
        Sort sort = Sort.by("id").ascending();
        Pageable pageable = PageRequest.of(from / size, size, sort);
        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);
        return getItemsWithBooking(items, userId);
    }

    @Transactional(readOnly = true)
    public List<ItemDto> searchItems(String text, long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        return itemMapper.toListDto(itemRepository.searchItems(text, pageable));
    }

    @Transactional
    public CommentDto addComment(Long userId, Long itemId, CommentDto commentDto) {

        Sort sort = Sort.by("start").descending();
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new ItemNotFoundException(itemId));
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Booking> bookings = bookingRepository.findAllByItemId(item.getId(), sort);
        if (bookings.stream().anyMatch(b -> b.getBooker().getId() == userId && b.getEnd().isBefore(LocalDateTime.now()))) {
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

    private User getOwner(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
    }

    public List<ItemWithBooking> getItemsWithBooking(List<Item> items, Long userId) {
        log.info("Items size {}", items.size());
        List<ItemWithBooking> withBookings = new ArrayList<>();
        if (items.isEmpty()) {
            return withBookings;
        }
        LocalDateTime date = LocalDateTime.now();
        Sort sort = Sort.by("start").descending();
        List<Booking> bookings = bookingRepository.findBookingByItemIn(items, sort).stream()
                .filter(b -> b.getStatus().equals(BookingStatus.APPROVED)).collect(Collectors.toList());
        List<Comment> comments = commentRepository.findAllByItemIn(items);
        for (Item item : items) {

            List<Booking> lastBookings = bookings.stream().filter(i -> i.getStart().isBefore(date)
                            && i.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());

            List<Booking> nextBookings = bookings.stream().filter(i -> i.getStart().isAfter(date)
                            && i.getItem().getId().equals(item.getId()))
                    .collect(Collectors.toList());
            ItemWithBooking itemWithBooking = itemMapper.toItemWithBooking(item);

            Booking lastBooking = null;
            Booking nextBooking = null;
            if (!lastBookings.isEmpty()) {
                lastBooking = lastBookings.get(0);
            }
            if (!nextBookings.isEmpty()) {
                nextBooking = nextBookings.get(nextBookings.size() - 1);
            }
            log.info("Last booking {}", lastBooking);
            log.info("Next booking {}", nextBooking);
            if (userId.equals(item.getOwner().getId())) {
                itemWithBooking.setNextBooking(bookingMapper.toBookingForItem(nextBooking));
                itemWithBooking.setLastBooking(bookingMapper.toBookingForItem(lastBooking));
            }
            itemWithBooking.setComments(commentMapper.toListDto(comments.stream()
                    .filter(c -> c.getItem().equals(item)).collect(Collectors.toList())));
            withBookings.add(itemWithBooking);
        }
        return withBookings;
    }


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


    private ItemWithBooking getItemWithBooking(Item item, Long userId) {
        List<CommentDto> comments = commentMapper.toListDto(commentRepository.findAllByItem(item));
        List<ItemWithBooking> bookings = getItemsWithBooking(List.of(item), userId);
        ItemWithBooking itemWithBooking = bookings.get(0);
        itemWithBooking.setComments(comments);
        return itemWithBooking;
    }
}

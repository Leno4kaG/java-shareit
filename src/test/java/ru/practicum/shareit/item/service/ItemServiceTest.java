package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.PageParamException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;
import ru.practicum.shareit.util.PageParamValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemServiceTest {

    private ItemRepositoryDB itemRepository = mock(ItemRepositoryDB.class);
    private UserRepositoryDB userRepository = mock(UserRepositoryDB.class);

    private BookingRepository bookingRepository = mock(BookingRepository.class);
    private ItemMapper itemMapper = mock(ItemMapper.class);
    private BookingMapper bookingMapper = mock(BookingMapper.class);

    private CommentRepository commentRepository = mock(CommentRepository.class);

    private CommentMapper commentMapper = mock(CommentMapper.class);

    private ItemRequestRepository itemRequestRepository = mock(ItemRequestRepository.class);

    private PageParamValidation<ItemWithBooking> pageParamValidation = mock(PageParamValidation.class);

    private PageParamValidation<ItemDto> pageParamValid = mock(PageParamValidation.class);

    private ItemService itemService = new ItemService(itemRepository, userRepository, bookingRepository, itemMapper,
            bookingMapper, commentRepository, commentMapper, itemRequestRepository, pageParamValidation, pageParamValid);

    @Test
    void createItemWhenDataCorrect() {
        long userId = 1L;
        ItemDto itemDto = ItemTestData.getItemDto();
        Item item = ItemTestData.getItem();

        when(itemMapper.fromDto(any())).thenReturn(item);
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRequestRepository.findById(itemDto.getRequestId()))
                .thenReturn(Optional.of(item.getRequest()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.createItem(itemDto, userId);

        assertEquals(itemDto, result);
    }

    @Test
    void createItemWhenDataError() {
        long userId = 1L;
        ItemDto itemDto = ItemTestData.getItemDto();
        itemDto.setRequestId(null);
        Item item = ItemTestData.getItem();

        when(itemMapper.fromDto(any())).thenReturn(item);
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));

        when(itemRepository.save(any())).thenReturn(item);

        ItemDto result = itemService.createItem(itemDto, userId);

        assertEquals(itemDto, result);
        assertNull(result.getRequestId());
    }

    @Test
    void createItemWhenUserNotFound() {
        long userId = 99L;
        ItemDto itemDto = ItemTestData.getItemDto();
        Item item = ItemTestData.getItem();

        when(itemMapper.fromDto(any())).thenReturn(item);
        when(userRepository.findById(any())).thenThrow(new UserNotFoundException(99L));

        UserNotFoundException result = assertThrows(UserNotFoundException.class,
                () -> itemService.createItem(itemDto, userId));

        assertEquals(userId, result.getUserId());
    }

    @Test
    void updateItemWhenDataCorrect() {
        long userId = 2L;
        ItemDto itemDto = ItemTestData.getItemDto();
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());


        when(commentRepository.findAllByItem(any())).thenReturn(comments);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemWithBooking result = itemService.updateItem(itemDto, userId);

        assertEquals(itemWithBooking, result);
    }

    @Test
    void updateItemWhenDataError() {
        long userId = 1L;
        ItemDto itemDto = ItemTestData.getItemDto();
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());


        when(commentRepository.findAllByItem(any())).thenReturn(comments);
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemNotFoundException result = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, userId));

        assertEquals(itemWithBooking.getId(), result.getItemId());
    }

    @Test
    void updateItemWhenItemNotFound() {
        long userId = 2L;
        ItemDto itemDto = ItemTestData.getItemDto();
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());


        when(commentRepository.findAllByItem(any())).thenReturn(comments);
        when(itemRepository.findById(any())).thenThrow(new ItemNotFoundException(itemWithBooking.getId()));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.save(any())).thenReturn(item);

        ItemNotFoundException result = assertThrows(ItemNotFoundException.class,
                () -> itemService.updateItem(itemDto, userId));

        assertEquals(itemWithBooking.getId(), result.getItemId());
    }

    @Test
    void getItemWhenItIsFound() {
        long userId = 2L;
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());

        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());

        ItemWithBooking result = itemService.getItem(itemWithBooking.getId(), userId);

        assertEquals(itemWithBooking, result);
    }

    @Test
    void getItemWhenIsNotFound() {
        long userId = 2L;
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());

        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.findById(any())).thenThrow(new ItemNotFoundException(itemWithBooking.getId()));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());

        ItemNotFoundException result = assertThrows(ItemNotFoundException.class,
                () -> itemService.getItem(itemWithBooking.getId(), userId));

        assertEquals(itemWithBooking.getId(), result.getItemId());
    }

    @Test
    void getAllItemsWhenDataCorrect() {
        long userId = 2L;
        Integer from = 0;
        Integer size = 1;
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());
        when(itemRepository.findAllByOwnerId(any())).thenReturn(List.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenReturn(List.of(itemWithBooking));

        List<ItemWithBooking> result = itemService.getAllItems(userId, from, size);

        assertEquals(itemWithBooking, result.get(0));
    }

    @Test
    void getAllItemsWhenPageParamError() {
        long userId = 2L;
        Integer from = -1;
        Integer size = 1;
        String errorMessage = "Индекс 1 страницы меньше 0";
        String errorMessage1 = "Количество страниц не должно быть = 0 или быть меньше 0";
        Item item = ItemTestData.getItem();
        ItemWithBooking itemWithBooking = ItemTestData.getItemWithBooking();
        List<Comment> comments = List.of(ItemTestData.getComment());
        when(itemRepository.findAllByOwnerId(any())).thenReturn(List.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(item.getOwner()));
        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(bookingRepository.findBookingByItemIn(any(), any())).thenReturn(List.of(BookingTestData.getBooking()));
        when(commentRepository.findAllByItemIn(any())).thenReturn(comments);
        when(itemMapper.toItemWithBooking(any())).thenReturn(itemWithBooking);
        when(commentMapper.toListDto(any())).thenReturn(itemWithBooking.getComments());
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenCallRealMethod();

        PageParamException resultError = assertThrows(PageParamException.class,
                () -> itemService.getAllItems(userId, from, size));

        assertEquals(errorMessage, resultError.getMessage());
        Integer from1 = 0;
        Integer size1 = 0;
        PageParamException resultError1 = assertThrows(PageParamException.class,
                () -> itemService.getAllItems(userId, from1, size1));

        assertEquals(errorMessage1, resultError1.getMessage());
    }

    @Test
    void searchItemsWhenDataCorrect() {
        long userId = 2L;
        Integer from = 0;
        Integer size = 1;
        String text = "test";
        Item item = ItemTestData.getItem();
        ItemDto itemDto = ItemTestData.getItemDto();

        when(itemRepository.searchItems(any())).thenReturn(List.of(item));
        when(itemMapper.toListDto(any())).thenReturn(List.of(itemDto));
        when(pageParamValid.getListWithPageParam(any(), any(), any())).thenCallRealMethod();

        List<ItemDto> list = itemService.searchItems(text, userId, from, size);

        assertEquals(itemDto, list.get(0));
    }

    @Test
    void searchItemsWhenPageParamCorrect() {
        long userId = 2L;
        Integer from = -1;
        Integer size = 1;
        String text = "test";
        Item item = ItemTestData.getItem();
        ItemDto itemDto = ItemTestData.getItemDto();
        String errorMessage = "Индекс 1 страницы меньше 0";

        when(itemRepository.searchItems(any())).thenReturn(List.of(item));
        when(itemMapper.toListDto(any())).thenReturn(List.of(itemDto));
        when(pageParamValid.getListWithPageParam(any(), any(), any())).thenCallRealMethod();

        PageParamException resultError = assertThrows(PageParamException.class,
                () -> itemService.searchItems(text, userId, from, size));

        assertEquals(errorMessage, resultError.getMessage());
    }

    @Test
    void addCommentWhenDataCorrect() {
        long userId = 1L;
        long itemId = 1L;
        Item item = ItemTestData.getItem();
        User user = UserTestData.getUser();
        List<Booking> bookings = List.of(BookingTestData.getBooking());
        Comment comment = ItemTestData.getComment();
        CommentDto commentDto = ItemTestData.getCommentDto();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemId(any(), any())).thenReturn(bookings);
        when(commentMapper.fromDto(any())).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);

        CommentDto result = itemService.addComment(userId, itemId, commentDto);

        assertEquals(commentDto, result);
    }

    @Test
    void addCommentWhenError() {
        long userId = 2L;
        long itemId = 1L;
        Item item = ItemTestData.getItem();
        User user = UserTestData.getUser();
        List<Booking> bookings = List.of(BookingTestData.getBooking());
        CommentDto commentDto = ItemTestData.getCommentDto();

        when(itemRepository.findById(any())).thenReturn(Optional.of(item));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(bookingRepository.findAllByItemId(any(), any())).thenReturn(bookings);

        CommentValidationException resultError = assertThrows(CommentValidationException.class,
                () -> itemService.addComment(userId, itemId, commentDto));

        assertEquals(CommentValidationException.class.getCanonicalName(), resultError.getClass().getCanonicalName());
    }
}
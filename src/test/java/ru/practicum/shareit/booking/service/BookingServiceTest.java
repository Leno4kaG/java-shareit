package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;
import ru.practicum.shareit.util.PageParamValidation;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class BookingServiceTest {

    private BookingRepository bookingRepository = mock(BookingRepository.class);

    private UserRepositoryDB userRepositoryDB = mock(UserRepositoryDB.class);

    private ItemRepositoryDB itemRepositoryDB = mock(ItemRepositoryDB.class);

    private BookingMapper bookingMapper = mock(BookingMapperImpl.class);

    private UserMapper userMapper = mock(UserMapperImpl.class);

    private ItemMapper itemMapper = mock(ItemMapperImpl.class);


    private PageParamValidation<BookingDto> pageParamValidation = mock(PageParamValidation.class);

    private final BookingService bookingService = new BookingService(bookingRepository, userRepositoryDB,
            itemRepositoryDB, bookingMapper, userMapper, itemMapper, pageParamValidation);

    @Test
    void createBookingWhenDataCorrect() {
        Long userId = 1L;
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(itemRepositoryDB.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(ItemTestData.getItem()));
        when(bookingRepository.save(any())).thenReturn(BookingTestData.getBooking());

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));

        when(bookingMapper.toBookingDto(any(BookingRequestDto.class))).thenReturn(bookingDto);
        when(userMapper.toDto(any(User.class))).thenReturn(bookingDto.getBooker());
        when(itemMapper.toDto(any())).thenReturn(bookingDto.getItem());

        BookingDto result = bookingService.createBooking(userId, bookingRequestDto);

        assertEquals(bookingDto, result);
    }

    @Test
    void createBookingWhenDataError() {
        Long userId = 1L;
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.empty());

        UserNotFoundException userError = assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingRequestDto));
        //when user not found
        assertEquals(userId, userError.getUserId());

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));

        when(itemRepositoryDB.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.empty());

        ItemNotFoundException itemError = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingRequestDto));
        //when item not found
        assertEquals(bookingRequestDto.getItemId(), itemError.getItemId());
        Item itemErrors = ItemTestData.getItemError();
        when(itemRepositoryDB.findById(bookingRequestDto.getItemId()))
                .thenReturn(Optional.of(itemErrors));
        ItemNotFoundException itemErrorOwner = assertThrows(ItemNotFoundException.class,
                () -> bookingService.createBooking(userId, bookingRequestDto));
        //when owner in item equals user id
        assertEquals(bookingRequestDto.getItemId(), itemErrorOwner.getItemId());

        itemErrors.setOwner(UserTestData.getOwner());
        BookingValidationException itemAvailableFalse = assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userId, bookingRequestDto));
        //when available in item equals false
        assertEquals(BookingValidationException.class.getCanonicalName(),
                itemAvailableFalse.getClass().getCanonicalName());
    }

    @Test
    void updateBookingWhenDataCorrect() {
        Long userId = 2L;
        Long bookingId = 1L;
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingRepository.save(booking))
                .thenReturn(booking);

        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.updateBooking(userId, bookingId, true);

        assertEquals(bookingDto, result);
    }

    @Test
    void updateBookingWhenDataError() {
        Long userId = 1L;
        Long bookingId = 1L;
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingNotFoundException resultError = assertThrows(BookingNotFoundException.class,
                () -> bookingService.updateBooking(userId, bookingId, true));

        assertEquals(bookingDto.getId(), resultError.getBookingId());

        booking.setStatus(BookingStatus.APPROVED);
        BookingValidationException resultValidError = assertThrows(BookingValidationException.class,
                () -> bookingService.updateBooking(2L, bookingId, true));
        assertEquals(BookingValidationException.class.getCanonicalName(),
                resultValidError.getClass().getCanonicalName());
    }

    @Test
    void getBookingWhenDataCorrect() {
        Long userId = 2L;
        Long bookingId = 1L;
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(bookingMapper.toDto(any(Booking.class))).thenReturn(bookingDto);

        BookingDto result = bookingService.getBooking(userId, bookingId);

        assertEquals(bookingDto, result);
    }

    @Test
    void getBookingWhenDataError() {
        Long userId = 3L;
        Long bookingId = 1L;
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();

        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingNotFoundException resultError = assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));

        assertEquals(bookingDto.getId(), resultError.getBookingId());
    }

    @Test
    void getBookingsForCurrentUserWhenDataCorrect() {
        Long userId = 2L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 1;
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toListDto(List.of(booking))).thenReturn(bookingDtoList);
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenReturn(bookingDtoList);

        List<BookingDto> resultList = bookingService.getBookingsForCurrentUser(userId, state, from, size);

        assertEquals(bookingDtoList, resultList);
    }

    @Test
    void getBookingsForCurrentUserWhenDataError() {
        Long userId = 2L;
        BookingState state = BookingState.ALL;
        Integer from = -1;
        Integer size = 1;
        String errorMessage = "Индекс 1 страницы меньше 0";
        String errorMessage1 = "Количество страниц не должно быть = 0 или быть меньше 0";
        Booking booking = BookingTestData.getBooking();
        BookingDto bookingDto = BookingTestData.getBookingDto();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toListDto(List.of(booking))).thenReturn(bookingDtoList);
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenCallRealMethod();

        PageParamException resultError = assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForCurrentUser(userId, state, from, size));

        assertEquals(errorMessage, resultError.getMessage());
        Integer from1 = 0;
        Integer size1 = 0;
        PageParamException resultError1 = assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForCurrentUser(userId, state, from1, size1));

        assertEquals(errorMessage1, resultError1.getMessage());
    }

    @Test
    void getBookingsForAllItemsWhenDataCorrect() {
        Long userId = 2L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 1;
        Booking booking = BookingTestData.getBooking();
        List<Item> items = List.of(ItemTestData.getItem());
        BookingDto bookingDto = BookingTestData.getBookingDto();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(itemRepositoryDB.findAllByOwnerId(any())).thenReturn(items);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toListDto(List.of(booking))).thenReturn(bookingDtoList);
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenReturn(bookingDtoList);

        List<BookingDto> resultList = bookingService.getBookingsForAllItems(userId, state, from, size);

        assertEquals(bookingDtoList, resultList);
    }

    @Test
    void getBookingsForAllItemsWhenDataError() {
        Long userId = 2L;
        BookingState state = BookingState.ALL;
        Integer from = -1;
        Integer size = 1;
        String errorMessage = "Индекс 1 страницы меньше 0";
        String errorMessage1 = "Количество страниц не должно быть = 0 или быть меньше 0";
        Booking booking = BookingTestData.getBooking();
        List<Item> items = List.of(ItemTestData.getItem());
        BookingDto bookingDto = BookingTestData.getBookingDto();
        List<BookingDto> bookingDtoList = List.of(bookingDto);
        when(userRepositoryDB.findById(anyLong())).thenReturn(Optional.of(UserTestData.getUser()));
        when(itemRepositoryDB.findAllByOwnerId(any())).thenReturn(items);
        when(bookingRepository.findAllByBookerId(anyLong(), any())).thenReturn(List.of(booking));
        when(bookingMapper.toListDto(List.of(booking))).thenReturn(bookingDtoList);
        when(pageParamValidation.getListWithPageParam(any(), any(), any())).thenCallRealMethod();

        PageParamException resultError = assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForAllItems(userId, state, from, size));

        assertEquals(errorMessage, resultError.getMessage());
        Integer from1 = 0;
        Integer size1 = 0;
        PageParamException resultError1 = assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForAllItems(userId, state, from1, size1));

        assertEquals(errorMessage1, resultError1.getMessage());
    }
}
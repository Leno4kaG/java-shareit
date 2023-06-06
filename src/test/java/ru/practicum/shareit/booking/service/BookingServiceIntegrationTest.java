package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.BookingValidationException;
import ru.practicum.shareit.exception.PageParamException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class BookingServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemService itemService;

    @Test
    void createBooking() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);
        assertEquals(bookingDto.getBooker().getId(), userDto1.getId());
    }

    @Test
    void createBookingValidationError() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setEnd(LocalDateTime.now().minusMinutes(5));

        assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userDto1.getId(), bookingRequestDto));
        bookingRequestDto.setEnd(bookingRequestDto.getEnd());
        assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userDto1.getId(), bookingRequestDto));
        bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(40));
        assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userDto1.getId(), bookingRequestDto));
    }

    @Test
    void createBookingValidationErrorDate() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusMinutes(2));
        bookingRequestDto.setEnd(bookingRequestDto.getStart().minusMinutes(1));

        assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userDto1.getId(), bookingRequestDto));
        bookingRequestDto.setStart(LocalDateTime.now().minusMinutes(2));
        bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(10));
        assertThrows(BookingValidationException.class,
                () -> bookingService.createBooking(userDto1.getId(), bookingRequestDto));
    }

    @Test
    void updateBookingTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);
        bookingDto.setEnd(LocalDateTime.now().plusMinutes(20));

        BookingDto bookingDto1 = bookingService.updateBooking(userDto.getId(), bookingDto.getId(), false);

        assertEquals(BookingStatus.REJECTED, bookingDto1.getStatus());
    }

    @Test
    void getBookingsForCurrentUserTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(userDto1.getId(), BookingState.ALL, null, null);
        assertThat(result.get(0).getId(), equalTo(bookingDto.getId()));
        assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForCurrentUser(userDto1.getId(), BookingState.CURRENT, -100, null));
        assertThrows(PageParamException.class,
                () -> bookingService.getBookingsForCurrentUser(userDto1.getId(), BookingState.CURRENT, 0, 0));
    }
}

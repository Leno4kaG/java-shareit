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
import ru.practicum.shareit.exception.UserNotFoundException;
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
    void createBookingError() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setEnd(LocalDateTime.now().minusMinutes(5));

        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(12L, bookingRequestDto));
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

        assertThrows(UserNotFoundException.class,
                () -> bookingService.createBooking(12L, bookingRequestDto));
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
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(userDto1.getId(), BookingState.ALL, 0, 10);
        assertThat(result.get(0).getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingsForCurrentUserTest1() throws InterruptedException {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        bookingRequestDto.setStart(LocalDateTime.now().plusSeconds(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusMinutes(4));

        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);
        Thread.sleep(1000);
        List<BookingDto> result = bookingService.getBookingsForCurrentUser(userDto1.getId(), BookingState.CURRENT, 0, 10);
        assertThat(result.get(0).getId(), equalTo(bookingDto.getId()));
    }

    @Test
    void getBookingsForAllItems() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());

        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);

        List<BookingDto> result = bookingService.getBookingsForAllItems(userDto.getId(),
                BookingState.ALL, 0, 1);
        assertEquals(List.of(bookingDto), result);
    }

    @Test
    void getBooking() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        BookingRequestDto bookingRequestDto = BookingTestData.getBookinReqDto();
        bookingRequestDto.setItemId(item.getId());
        BookingDto bookingDto = bookingService.createBooking(userDto1.getId(), bookingRequestDto);

        BookingDto result = bookingService.getBooking(userDto1.getId(), bookingDto.getId());

        assertEquals(bookingDto, result);
    }
}

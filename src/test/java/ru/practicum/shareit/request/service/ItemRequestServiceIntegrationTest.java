package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemRequestServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemService itemService;


    @Test
    void createRequestTest() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        assertEquals(ItemRequestTestData.getItemReqDto().getDescription(), itemRequestDto.getDescription());

        assertThrows(UserNotFoundException.class, () -> itemRequestService.createRequest(12L,
                ItemRequestTestData.getItemReqDto()));
    }

    @Test
    void getItemRequestByIdTest() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        UserDto booker = userService.createUser(UserTestData.getUserDtoOwner());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        ItemDto itemDto = ItemTestData.getItemDto();
        itemDto.setRequestId(itemRequestDto.getId());
        BookingRequestDto bookingDtoReq = BookingTestData.getBookinReqDto();
        ItemDto itemDto1 = itemService.createItem(itemDto, user.getId());
        bookingDtoReq.setItemId(itemDto1.getId());
        BookingDto bookingDto = bookingService.createBooking(booker.getId(), bookingDtoReq);

        RequestInfoDto result = itemRequestService.getItemRequestById(user.getId(), itemRequestDto.getId());
        assertEquals(itemRequestDto.getId(), result.getId());
        assertEquals(itemRequestDto.getDescription(), result.getDescription());

        assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getItemRequestById(12L, 12L));
        assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(user.getId(), 12L));
    }

    @Test
    void getAllRequestsForOwner() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        UserDto booker = userService.createUser(UserTestData.getUserDtoOwner());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        ItemDto itemDto = ItemTestData.getItemDto();
        itemDto.setRequestId(itemRequestDto.getId());
        BookingRequestDto bookingDtoReq = BookingTestData.getBookinReqDto();
        ItemDto itemDto1 = itemService.createItem(itemDto, user.getId());
        bookingDtoReq.setItemId(itemDto1.getId());
        BookingDto bookingDto = bookingService.createBooking(booker.getId(), bookingDtoReq);

        List<RequestInfoDto> list = itemRequestService.getAllRequestsForOwner(user.getId());

        assertEquals(itemRequestDto.getId(), list.get(0).getId());
        assertEquals(itemRequestDto.getDescription(), list.get(0).getDescription());
    }

    @Test
    void getAllRequests() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        UserDto user1 = userService.createUser(UserTestData.getUserDtoOwner());
        ItemRequestDto reqDto = ItemRequestTestData.getItemReqDto();
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(), reqDto);

        List<RequestInfoDto> list = itemRequestService.getAllRequests(user1.getId(), 0, 1);

        assertEquals(itemRequestDto.getDescription(), list.get(0).getDescription());
    }

    @Test
    void getItemRequestById() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        RequestInfoDto result = itemRequestService.getItemRequestById(user.getId(), itemRequestDto.getId());

        assertEquals(itemRequestDto.getId(), result.getId());
    }
}
package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
    void getAllRequestsForOwner() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        List<ItemRequestDto> list = itemRequestService.getAllRequestsForOwner(user.getId());

        assertEquals(List.of(itemRequestDto), list);
    }

    @Test
    void getAllRequests() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        UserDto user1 = userService.createUser(UserTestData.getUserDtoOwner());
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        itemRequestDto.setRequestorId(user1.getId());
        itemRequestDto = itemRequestService.createRequest(user.getId(), itemRequestDto);
        List<ItemRequestDto> list = itemRequestService.getAllRequests(user1.getId(), 0, 1);

        assertEquals(List.of(itemRequestDto), list);
    }

    @Test
    void getItemRequestById() {
        UserDto user = userService.createUser(UserTestData.getUserDto());
        ItemRequestDto itemRequestDto = itemRequestService.createRequest(user.getId(),
                ItemRequestTestData.getItemReqDto());
        ItemRequestDto result = itemRequestService.getItemRequestById(user.getId(), itemRequestDto.getId());

        assertEquals(itemRequestDto, result);
    }
}
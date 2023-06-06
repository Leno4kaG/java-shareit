package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.PageParamException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ItemServiceIntegrationTest {

    @Autowired
    private UserService userService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemService itemService;


    @Test
    void getAllItemsTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());
        List<ItemWithBooking> items = itemService.getAllItems(userDto.getId(), null, null);
        assertThat(items.get(0).getId(), equalTo(item.getId()));
        assertThrows(PageParamException.class,
                () -> itemService.getAllItems(userDto.getId(), -100, null));
        assertThrows(PageParamException.class,
                () -> itemService.getAllItems(userDto.getId(), 0, 0));

    }
}

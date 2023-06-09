package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.CommentValidationException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    void getItemTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());

        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());

        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());

        ItemWithBooking result = itemService.getItem(item.getId(), userDto.getId());

        assertEquals(item.getName(), result.getName());
        assertEquals(item.getId(), result.getId());

        assertThrows(ItemNotFoundException.class, () -> itemService.getItem(12L, userDto.getId()));
    }

    @Test
    void getAllItemsTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());

        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());

        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());

        List<ItemWithBooking> items = itemService.getAllItems(userDto.getId(), 0, 10);
        assertThat(items.get(0).getId(), equalTo(item.getId()));
        UserDto userDto1 = userService.createUser(UserTestData.getUserDtoOwner());

        List<ItemWithBooking> items1 = itemService.getAllItems(userDto.getId(), 0, 1);
        assertThat(items1.size(), equalTo(1));
        ItemDto item1 = itemService.createItem(ItemTestData.getItemDto(), userDto1.getId());
        itemRequestService.createRequest(userDto1.getId(), ItemRequestTestData.getItemReqDto());
        List<ItemWithBooking> items2 = itemService.getAllItems(userDto.getId(), 0, 1);
        assertThat(items2.size(), equalTo(0));
    }

    @Test
    void searchItemsTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());

        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());

        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());

        List<ItemDto> result = itemService.searchItems("test", userDto.getId(), 0, 1);

        assertEquals(List.of(item), result);

        List<ItemDto> listEmpty = itemService.searchItems("щетка", userDto.getId(), 0, 1);

        assertEquals(0, listEmpty.size());
    }

    @Test
    void addCommentTest() {
        UserDto userDto = userService.createUser(UserTestData.getUserDto());
        itemRequestService.createRequest(userDto.getId(), ItemRequestTestData.getItemReqDto());
        ItemDto item = itemService.createItem(ItemTestData.getItemDto(), userDto.getId());

        assertThrows(CommentValidationException.class,
                () -> itemService.addComment(userDto.getId(), item.getId(), new CommentDto()));
    }
}

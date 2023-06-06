package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ItemRequestServiceTest {

    private ItemRequestRepository requestRepository = mock(ItemRequestRepository.class);
    private UserRepositoryDB userRepository = mock(UserRepositoryDB.class);
    private ItemRequestMapper requestMapper = mock(ItemRequestMapper.class);

    private ItemRepositoryDB itemRepository = mock(ItemRepositoryDB.class);

    private ItemService itemService = mock(ItemService.class);

    private ItemRequestService itemRequestService = new ItemRequestService(requestRepository, userRepository,
            requestMapper, itemRepository, itemService);

    @Test
    void createRequestWhenDataCorrect() {
        User user = UserTestData.getUser();
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestMapper.fromDto(any())).thenReturn(itemRequest);
        when(requestMapper.toDto(any())).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.createRequest(user.getId(), itemRequestDto);

        assertEquals(itemRequestDto, result);
    }

    @Test
    void createRequestWhenUserNotFound() {
        User user = UserTestData.getUser();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();

        when(userRepository.findById(any())).thenThrow(new UserNotFoundException(user.getId()));

        UserNotFoundException resultError = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.createRequest(user.getId(), itemRequestDto));

        assertEquals(user.getId(), resultError.getUserId());
    }

    @Test
    void getAllRequestsForOwnerTest() {
        User user = UserTestData.getUser();
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        List<Item> items = List.of(ItemTestData.getItem());
        List<ItemWithBooking> itemsWithBooking = List.of(ItemTestData.getItemWithBooking());

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequestId(anyLong(), any())).thenReturn(List.of(itemRequest));
        when(requestMapper.toListDto(any())).thenReturn(List.of(itemRequestDto));
        when(itemRepository.findAllByRequestId(any())).thenReturn(items);
        when(itemService.getItemsWithBooking(any(), anyLong())).thenReturn(itemsWithBooking);

        List<ItemRequestDto> result = itemRequestService.getAllRequestsForOwner(user.getId());

        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    void getAllRequestsForOwnerWhenUserNotFound() {
        User user = UserTestData.getUser();

        when(userRepository.findById(any())).thenThrow(new UserNotFoundException(user.getId()));

        UserNotFoundException resultError = assertThrows(UserNotFoundException.class,
                () -> itemRequestService.getAllRequestsForOwner(user.getId()));

        assertEquals(user.getId(), resultError.getUserId());
    }

    @Test
    void getAllRequestsTest() {
        User user = UserTestData.getOwner();
        Integer from = 0;
        Integer size = 1;
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        List<Item> items = List.of(ItemTestData.getItem());
        List<ItemWithBooking> itemsWithBooking = List.of(ItemTestData.getItemWithBooking());
        Pageable pageable = PageRequest.of(0, 1);
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest).subList(0, 1), pageable, 1);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(requestMapper.toDto(any())).thenReturn(itemRequestDto);
        when(requestMapper.toListDto(any())).thenReturn(List.of(itemRequestDto));
        when(itemRepository.findAllByRequestId(any())).thenReturn(items);
        when(itemService.getItemsWithBooking(any(), anyLong())).thenReturn(itemsWithBooking);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId(), from, size);

        assertEquals(itemRequestDto, result.get(0));
    }

    @Test
    void getAllRequestsTestError() {
        User user = UserTestData.getUser();
        Integer from = 0;
        Integer size = 1;
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();
        List<Item> items = List.of(ItemTestData.getItem());
        List<ItemWithBooking> itemsWithBooking = List.of(ItemTestData.getItemWithBooking());
        Pageable pageable = PageRequest.of(0, 1);
        Page<ItemRequest> page = new PageImpl<>(List.of(itemRequest).subList(0, 1), pageable, 1);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(requestMapper.toDto(any())).thenReturn(itemRequestDto);
        when(requestMapper.toListDto(any())).thenReturn(List.of(itemRequestDto));
        when(itemRepository.findAllByRequestId(any())).thenReturn(items);
        when(itemService.getItemsWithBooking(any(), anyLong())).thenReturn(itemsWithBooking);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(user.getId(), from, size);

        assertTrue(result.isEmpty());
    }

    @Test
    void getItemRequestByIdWhenRequestFound() {
        User user = UserTestData.getOwner();
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toDto(any())).thenReturn(itemRequestDto);

        ItemRequestDto result = itemRequestService.getItemRequestById(user.getId(), itemRequest.getId());

        assertEquals(itemRequestDto, result);
    }

    @Test
    void getItemRequestByIdWhenRequestNotFound() {
        User user = UserTestData.getOwner();
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        ItemRequestDto itemRequestDto = ItemRequestTestData.getItemReqDto();

        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(requestRepository.findById(anyLong())).thenThrow(new ItemRequestNotFoundException(itemRequest.getId()));

        ItemRequestNotFoundException resultError = assertThrows(ItemRequestNotFoundException.class,
                () -> itemRequestService.getItemRequestById(user.getId(), itemRequest.getId()));

        assertEquals(itemRequestDto.getId(), resultError.getItemRequestId());
    }
}
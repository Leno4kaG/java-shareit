package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepositoryDB userRepository;
    private final ItemRequestMapper requestMapper;

    private final ItemRepositoryDB itemRepository;

    private final ItemService itemService;


    @Transient
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        ItemRequest itemRequest = requestMapper.fromDto(itemRequestDto);
        itemRequest.setRequest(owner);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Item request create {}", itemRequest);
        return requestMapper.toDto(requestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequestsForOwner(Long userId) {
        User request = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("Get all item requests by user id {}", userId);
        Sort sort = Sort.by("created").descending();
        List<ItemRequestDto> requestDtoList = requestMapper.toListDto(requestRepository.findAllByRequestId(request.getId(), sort));
        requestDtoList.forEach(req -> req.getItems().addAll(getItemsByReq(req.getRequestorId(), userId)));
        return requestDtoList;
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Sort sort = Sort.by("created").descending();
        Pageable sortedByCreated = PageRequest.of(from, size, sort);
        log.info("Get all request user id = {} and page = {} and size = {}", userId, from, size);
        List<ItemRequestDto> itemRequests = requestRepository.findAll(sortedByCreated).map(requestMapper::toDto)
                .stream().filter(req -> !req.getRequestorId().equals(userId)).collect(Collectors.toList());
        log.info("Items req size = {}", itemRequests.size());
        itemRequests.forEach(req -> req.getItems().addAll(getItemsByReq(req.getRequestorId(), userId)));
        return itemRequests;
    }

    @Transactional(readOnly = true)
    public ItemRequestDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("Get item requests by id = {}", requestId);
        ItemRequestDto itemRequestDto = requestMapper.toDto(requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(requestId)));
        itemRequestDto.getItems().addAll(getItemsByReq(itemRequestDto.getRequestorId(), userId));
        return itemRequestDto;
    }

    private List<ItemWithBooking> getItemsByReq(Long requestId, Long userId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        log.info("Items size = {}", items.size());
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return itemService.getItemsWithBooking(items, userId);
    }
}

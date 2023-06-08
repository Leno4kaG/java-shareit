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
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestInfoDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper requestMapper;

    private final ItemRepository itemRepository;

    private final ItemMapper itemMapper;


    @Transient
    public RequestInfoDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        ItemRequest itemRequest = requestMapper.fromDto(itemRequestDto);
        itemRequest.setRequest(owner);
        itemRequest.setCreated(LocalDateTime.now());
        log.info("Item request create {}", itemRequest);
        return requestMapper.toInfoDto(requestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    public List<RequestInfoDto> getAllRequestsForOwner(Long userId) {
        User request = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("Get all item requests by user id {}", userId);
        Sort sort = Sort.by("created").descending();
        List<ItemRequest> requests = requestRepository.findAllByRequestId(request.getId(), sort);
        requests.forEach(req -> req.getItems().addAll(getItemsByReq(req.getRequest().getId(), userId)));
        return requestMapper.toListInfoDto(requests);
    }

    @Transactional(readOnly = true)
    public List<RequestInfoDto> getAllRequests(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Sort sort = Sort.by("created").descending();
        Pageable sortedByCreated = PageRequest.of(from, size, sort);
        log.info("Get all request user id = {} and page = {} and size = {}", userId, from, size);
        List<ItemRequest> itemRequests = requestRepository.findByRequestIdNot(userId, sortedByCreated);
        log.info("Items req size = {}", itemRequests.size());
        itemRequests.forEach(req -> req.getItems().addAll(getItemsByReq(req.getRequest().getId(), userId)));
        return requestMapper.toListInfoDto(itemRequests);
    }

    @Transactional(readOnly = true)
    public RequestInfoDto getItemRequestById(Long userId, Long requestId) {
        userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        log.info("Get item requests by id = {}", requestId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new ItemRequestNotFoundException(requestId));
        itemRequest.getItems().addAll(getItemsByReq(itemRequest.getRequest().getId(), userId));
        return requestMapper.toInfoDto(itemRequest);
    }

    private List<ItemDto> getItemsByReq(Long requestId, Long userId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        log.info("Items size = {}", items.size());
        if (items.isEmpty()) {
            return Collections.emptyList();
        }
        return itemMapper.toListDto(items);
    }
}

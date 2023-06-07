package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemReqMapperTest {
    ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void toDto() {
        User user = userRepository.save(UserTestData.getUser());
        Item item = ItemTestData.getItem();
        item.setOwner(user);
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        itemRequest.setRequest(user);
        itemRequest = itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequest);
        item = itemRepository.save(item);
        Booking booking = BookingTestData.getBookingForDBtest();
        booking.setItem(item);
        booking.setBooker(user);
        bookingRepository.save(booking);

        ItemRequest req = itemRequestRepository.findById(itemRequest.getId()).get();

        ItemRequestDto result = itemRequestMapper.toDto(req);

        assertEquals(itemRequest.getId(), result.getId());
    }

}

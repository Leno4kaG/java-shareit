package ru.practicum.shareit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.data.BookingTestData;
import ru.practicum.shareit.data.ItemRequestTestData;
import ru.practicum.shareit.data.ItemTestData;
import ru.practicum.shareit.data.UserTestData;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepositoryDB;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepositoryDB;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class RepositoryTest {

    @Autowired
    TestEntityManager em;
    @Autowired
    BookingRepository bookingRepository;
    @Autowired
    UserRepositoryDB userRepository;
    @Autowired
    ItemRepositoryDB itemRepository;

    @Autowired
    ItemRequestRepository itemRequestRepository;

    @Test
    void findAllCurrentBooking() {
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

        booking = bookingRepository.save(booking);
        List<Booking> bookings = bookingRepository.findAllCurrent(user);
        assertEquals(List.of(booking), bookings);
    }

    @Test
    void searchItems() {
        User user = userRepository.save(UserTestData.getUser());
        Item item = ItemTestData.getItem();
        item.setOwner(user);
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        itemRequest.setRequest(user);
        itemRequest = itemRequestRepository.save(itemRequest);
        item.setRequest(itemRequest);
        item = itemRepository.save(item);
        List<Item> items = itemRepository.searchItems("test");
        assertEquals(List.of(item), items);
    }

    @Test
    void findAllByRequestId() {
        User user = userRepository.save(UserTestData.getUser());
        ItemRequest itemRequest = ItemRequestTestData.getItemReq();
        itemRequest.setRequest(user);
        itemRequest = itemRequestRepository.save(itemRequest);
        Sort sort = Sort.by("created").descending();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestId(itemRequest.getId(), sort);
        assertEquals(List.of(itemRequest), itemRequests);
    }
}
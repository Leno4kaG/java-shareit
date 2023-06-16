package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b where b.booker.id = :booker and b.start < now() " +
            "and b.end > now()")
    List<Booking> findAllCurrent(@Param("booker") Long booker, Pageable pageable);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Pageable pageable);

    List<Booking> findAllByItemId(Long itemId, Pageable pageable);

    List<Booking> findAllByItemId(Long itemId, Sort sort);

    @Query(value = "SELECT b FROM Booking b where b.item = :item and b.start < now() " +
            "and b.end > now()")
    List<Booking> findAllCurrentByItem(@Param("item") Item item, Pageable pageable);

    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime date, Pageable pageable);

    List<Booking> findAllByItemIdAndStartAfter(Long itemId, LocalDateTime date, Pageable pageable);

    List<Booking> findBookingByItemIdAndStatus(Long itemId, BookingStatus status, Pageable pageable);

    List<Booking> findBookingByItemIn(List<Item> items, Sort sort);

}

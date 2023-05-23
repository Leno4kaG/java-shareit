package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    @Query(value = "SELECT b FROM Booking b where b.booker = :booker and b.start < now() " +
            "and b.end > now() order by b.start DESC")
    List<Booking> findAllCurrent(@Param("booker") User booker);

    List<Booking> findAllByBookerIdAndEndBefore(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStartAfter(Long bookerId, LocalDateTime date, Sort sort);

    List<Booking> findAllByBookerIdAndStatus(Long bookerId, BookingStatus status, Sort sort);

    List<Booking> findAllByItemId(Long itemId, Sort sort);

    @Query(value = "SELECT b FROM Booking b where b.item = :item and b.start < now() " +
            "and b.end > now() order by b.start DESC")
    List<Booking> findAllCurrentByItem(@Param("item") Item item);

    List<Booking> findAllByItemIdAndEndBefore(Long itemId, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdAndStartAfter(Long itemId, LocalDateTime date, Sort sort);

    List<Booking> findBookingByItemIdAndStatus(Long itemId, BookingStatus status, Sort sort);

    List<Booking> findBookingByItemIdAndStatusAndStartBefore(Long itemId, BookingStatus status, LocalDateTime now, Sort sort);

    List<Booking> findBookingByItemIdAndStatusAndStartAfter(Long itemId, BookingStatus status, LocalDateTime now, Sort sort);

}

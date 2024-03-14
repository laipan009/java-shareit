package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b WHERE b.id = :id")
    Booking findBookingByIdWithItemAndBookerEagerly(@Param("id") Integer id);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :booker_id ORDER BY b.start DESC")
    Slice<Booking> findAllBookingsWithItemAndBookerSortedByStartDateDesc(@Param("booker_id") Integer id, Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Slice<Booking> findAllCurrentBookingsWithItemAndBooker(Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Slice<Booking> findAllPastBookingsWithItemAndBooker(Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    Slice<Booking> findAllFutureBookingsWithItemAndBooker(Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookingStatus = 'WAITING' ORDER BY b.start DESC")
    Slice<Booking> findWaitingBookingsSortedByStartDateDesc(Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookingStatus = 'REJECTED' AND b.booker.id = :booker_id " +
            "ORDER BY b.start DESC")
    Slice<Booking> findRejectedBookingsSortedByStartDateDesc(@Param("booker_id") Integer id, Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.bookingStatus = 'REJECTED' AND b.item.owner.id = :owner_id " +
            "ORDER BY b.start DESC")
    Slice<Booking> findRejectedBookingsByOwnerSortedByStartDateDesc(@Param("owner_id") Integer owner_id, Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    Slice<Booking> findByItem_Owner_IdOrderByStartDesc(Integer ownerId, Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    Slice<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer ownerId, LocalDateTime start, LocalDateTime end, Pageable page);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND NOT b.bookingStatus = 'REJECTED' " +
            "ORDER BY b.end DESC")
    Page<Booking> findLastBookingByItemIdExcludingRejected(@Param("itemId") Integer itemId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND NOT b.bookingStatus = 'REJECTED' " +
            "ORDER BY b.start ASC")
    Page<Booking> findNextBookingByItemIdExcludingRejected(@Param("itemId") Integer itemId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "ORDER BY b.item.id ASC")
    Slice<Booking> findBookingsByUserId(@Param("userId") Integer userId, Pageable page);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.end < CURRENT_TIMESTAMP")
    boolean existsByItemIdAndUserIdAndEnded(@Param("itemId") Integer itemId, @Param("userId") Integer userId);

    @EntityGraph(attributePaths = {"item", "booker", "item.owner"})
    List<Booking> findByItem_Owner_IdAndBookingStatusNotOrderByStartDesc(Integer ownerId, BookingStatus bookingStatus);
}
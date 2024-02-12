package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.id = :id")
    Booking findBookingByIdWithItemAndBookerEagerly(@Param("id") Integer id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker WHERE b.booker.id = :booker_id ORDER BY b.start DESC")
    List<Booking> findAllBookingsWithItemAndBookerSortedByStartDateDesc(@Param("booker_id") Integer id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentBookingsWithItemAndBooker();

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.end < CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastBookingsWithItemAndBooker();

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.start > CURRENT_TIMESTAMP " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureBookingsWithItemAndBooker();

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.bookingStatus = 'WAITING' ORDER BY b.start DESC")
    List<Booking> findWaitingBookingsSortedByStartDateDesc();

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.bookingStatus = 'REJECTED' AND b.booker.id = :booker_id " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsSortedByStartDateDesc(@Param("booker_id") Integer id);

    @Query("SELECT b FROM Booking b JOIN FETCH b.item JOIN FETCH b.booker " +
            "WHERE b.bookingStatus = 'REJECTED' AND b.item.owner.id = :owner_id " +
            "ORDER BY b.start DESC")
    List<Booking> findRejectedBookingsByOwnerSortedByStartDateDesc(@Param("owner_id") Integer owner_id);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItem_Owner_IdOrderByStartDesc(Integer ownerId);

    @EntityGraph(attributePaths = {"item", "booker"})
    List<Booking> findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
            Integer ownerId, LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start < CURRENT_TIMESTAMP " +
            "AND NOT b.bookingStatus = 'REJECTED' " +
            "ORDER BY b.end DESC")
    Page<Booking> findLastBookingByItemIdExcludingRejected(@Param("itemId") Integer itemId, Pageable pageable);

    @EntityGraph(attributePaths = {"item", "booker"})
    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId " +
            "AND b.start > CURRENT_TIMESTAMP " +
            "AND NOT b.bookingStatus = 'REJECTED' " +
            "ORDER BY b.start ASC")
    Page<Booking> findNextBookingByItemIdExcludingRejected(@Param("itemId") Integer itemId, Pageable pageable);

    @Query("SELECT b FROM Booking b " +
            "WHERE b.item.owner.id = :userId " +
            "ORDER BY b.item.id ASC")
    List<Booking> findBookingsByUserId(@Param("userId") Integer userId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b " +
            "WHERE b.item.id = :itemId " +
            "AND b.booker.id = :userId " +
            "AND b.end < CURRENT_TIMESTAMP")
    boolean existsByItemIdAndUserIdAndEnded(@Param("itemId") Integer itemId, @Param("userId") Integer userId);
}

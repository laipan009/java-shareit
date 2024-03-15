package ru.practicum.shareit.user.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.user.history.UserBookingHistory;

import java.util.List;

public interface UserBookingHistoryRepository extends JpaRepository<UserBookingHistory, Integer> {

    @Query("SELECT ubh FROM UserBookingHistory ubh WHERE ubh.userId = :userId ORDER BY ubh.startDate DESC")
    List<UserBookingHistory> findBookingsByUserIdOrderByStartDateDesc(Integer userId);

}
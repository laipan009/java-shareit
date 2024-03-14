package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@Transactional
public class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private EntityManager entityManager;

    private User user;
    private Item item;
    private Booking booking;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);

        booking = new Booking();
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setBookingStatus(BookingStatus.WAITING);
        entityManager.persist(booking);

        entityManager.flush();
    }

    @Test
    void getBookingByUserWhenBookingsExist() {
        List<BookingResponseDto> bookings = bookingService.getBookingByUser(String.valueOf(BookingState.ALL),
                user.getId(), 0, 2);

        assertFalse(bookings.isEmpty(), "Bookings should not be empty");
        assertEquals(1, bookings.size(), "There should be one booking");
        assertEquals(booking.getId(), bookings.get(0).getId(), "Booking ID should match");
    }

    @Test
    void getBookingByOwnerWhenBookingsExist() {
        List<BookingResponseDto> bookings = bookingService.getBookingByOwner(String.valueOf(BookingState.ALL),
                user.getId(), 0, 2);

        assertFalse(bookings.isEmpty(), "Bookings should not be empty");
        assertEquals(1, bookings.size(), "There should be one booking");
        assertEquals(booking.getId(), bookings.get(0).getId(), "Booking ID should match");
    }
}
package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookingRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BookingRepository bookingRepository;

    private User user;
    private Item item;
    private Booking currentBooking;
    private Booking pastBooking;
    private Booking futureBooking;
    private Booking waitingBooking;
    private PageRequest pageRequest;
    LocalDateTime now = LocalDateTime.now();
    private User booker;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        booker= new User();
        booker.setName("Booker Name");
        booker.setEmail("booker@example.com");
        entityManager.persist(booker);

        item = new Item();
        item.setName("Test Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);

        pastBooking = new Booking();
        pastBooking.setItem(item);
        pastBooking.setBooker(booker);
        pastBooking.setStart(now.minusDays(2));
        pastBooking.setEnd(now.minusDays(1));
        pastBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(pastBooking);

        currentBooking = new Booking();
        currentBooking.setItem(item);
        currentBooking.setBooker(booker);
        currentBooking.setStart(now.minusHours(1));
        currentBooking.setEnd(now.plusHours(1));
        currentBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(currentBooking);

        futureBooking = new Booking();
        futureBooking.setItem(item);
        futureBooking.setBooker(booker);
        futureBooking.setStart(now.plusDays(1));
        futureBooking.setEnd(now.plusDays(2));
        futureBooking.setBookingStatus(BookingStatus.APPROVED);
        entityManager.persist(futureBooking);

        waitingBooking = new Booking();
        waitingBooking.setItem(item);
        waitingBooking.setBooker(booker);
        waitingBooking.setStart(now.plusDays(3));
        waitingBooking.setEnd(now.plusDays(4));
        waitingBooking.setBookingStatus(BookingStatus.WAITING);
        entityManager.persist(waitingBooking);

        pageRequest = PageRequest.of(0, 10);
    }

    @Test
    void whenFindLastBookingByItemIdExcludingRejected_thenSuccess() {
        Booking foundBooking = bookingRepository.findLastBookingByItemIdExcludingRejected(item.getId(), pageRequest).getContent().get(0);

        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(currentBooking.getId());
    }

    @Test
    void whenFindBookingByIdWithItemAndBookerEagerly_thenSuccess() {
        Booking foundBooking = bookingRepository.findBookingByIdWithItemAndBookerEagerly(currentBooking.getId());


        assertThat(foundBooking).isNotNull();
        assertThat(foundBooking.getId()).isEqualTo(currentBooking.getId());
        assertThat(foundBooking.getItem()).isNotNull();
        assertThat(foundBooking.getItem().getId()).isEqualTo(item.getId());
        assertThat(foundBooking.getBooker()).isNotNull();
        assertThat(foundBooking.getBooker().getId()).isEqualTo(booker.getId());
    }

    @Test
    void whenFindAllBookingsWithItemAndBookerSortedByStartDateDesc_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findAllBookingsWithItemAndBookerSortedByStartDateDesc(booker.getId(), pageRequest);
        assertThat(bookings.getContent()).hasSize(4).extracting("id")
                .containsExactlyInAnyOrder(futureBooking.getId(), currentBooking.getId(), pastBooking.getId(), waitingBooking.getId());
    }

    @Test
    void whenFindAllCurrentBookingsWithItemAndBooker_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findAllCurrentBookingsWithItemAndBooker(pageRequest);
        assertThat(bookings.getContent()).hasSize(1).extracting("id")
                .containsExactly(currentBooking.getId());
    }

    @Test
    void whenFindAllPastBookingsWithItemAndBooker_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findAllPastBookingsWithItemAndBooker(pageRequest);
        assertThat(bookings.getContent()).hasSize(1).extracting("id")
                .containsExactly(pastBooking.getId());
    }

    @Test
    void whenFindAllFutureBookingsWithItemAndBooker_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findAllFutureBookingsWithItemAndBooker(pageRequest);
        assertThat(bookings.getContent()).hasSize(2).extracting("id")
                .containsExactlyInAnyOrder(futureBooking.getId(), waitingBooking.getId());
    }

    @Test
    void whenFindWaitingBookingsSortedByStartDateDesc_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findWaitingBookingsSortedByStartDateDesc(pageRequest);
        assertThat(bookings.getContent()).hasSize(1).extracting("id")
                .containsExactly(waitingBooking.getId());
    }

    @Test
    void whenFindRejectedBookingsSortedByStartDateDesc_thenSuccess() {
        waitingBooking.setBookingStatus(BookingStatus.REJECTED);
        entityManager.flush();
        Slice<Booking> bookings = bookingRepository.findRejectedBookingsSortedByStartDateDesc(booker.getId(), pageRequest);
        assertThat(bookings.getContent()).hasSize(1).extracting("id")
                .containsExactly(waitingBooking.getId());
    }

    @Test
    void whenFindRejectedBookingsByOwnerSortedByStartDateDesc_thenSuccess() {
        waitingBooking.setBookingStatus(BookingStatus.REJECTED);
        entityManager.flush();
        Slice<Booking> bookings = bookingRepository.findRejectedBookingsByOwnerSortedByStartDateDesc(user.getId(), pageRequest);
        assertThat(bookings.getContent()).hasSize(1).extracting("id")
                .containsExactly(waitingBooking.getId());
    }

    @Test
    void whenFindByItem_Owner_IdOrderByStartDesc_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(user.getId(), pageRequest);
        assertThat(bookings.getContent()).isNotEmpty();
    }

    @Test
    void whenFindByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc_thenSuccess() {
        LocalDateTime start = LocalDateTime.now().minusMinutes(30);
        LocalDateTime end = LocalDateTime.now().plusMinutes(30);
        Slice<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(user.getId(), start, end, pageRequest);
        assertThat(bookings.getContent()).isNotEmpty();
    }

    @Test
    void whenFindNextBookingByItemIdExcludingRejected_thenSuccess() {
        Page<Booking> bookings = bookingRepository.findNextBookingByItemIdExcludingRejected(item.getId(), pageRequest);
        assertThat(bookings.getContent()).hasSize(2).extracting("id")
                .containsExactly(futureBooking.getId(), waitingBooking.getId());
    }

    @Test
    void whenFindBookingsByUserId_thenSuccess() {
        Slice<Booking> bookings = bookingRepository.findBookingsByUserId(user.getId(), pageRequest);
        assertThat(bookings.getContent()).isNotEmpty();
    }

    @Test
    void whenExistsByItemIdAndUserIdAndEnded_thenSuccess() {
        boolean exists = bookingRepository.existsByItemIdAndUserIdAndEnded(item.getId(), booker.getId());
        assertThat(exists).isTrue();
    }

    @Test
    void whenFindByItem_Owner_IdAndBookingStatusNotOrderByStartDesc_thenSuccess() {
        List<Booking> bookings = bookingRepository.findByItem_Owner_IdAndBookingStatusNotOrderByStartDesc(user.getId(), BookingStatus.REJECTED);
        assertThat(bookings).isNotEmpty();
    }
}
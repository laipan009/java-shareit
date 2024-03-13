package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserStorage userStorage;
    @Mock
    private ItemStorage itemStorage;
    @Mock
    private BookingMapper bookingMapper;
    @InjectMocks
    private BookingService bookingService;

    private BookingRequestDto bookingRequestDto;
    private User booker;
    private Item item;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private final Integer bookerId = 1;
    private final Integer itemId = 1;

    @BeforeEach
    void setUp() {
        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(itemId)
                .build();

        booker = new User();
        booker.setId(bookerId);

        item = new Item();
        item.setId(itemId);
        item.setAvailable(true);
        item.setOwner(new User());

        booking = new Booking();
        booking.setId(1);
        booking.setItem(item);
        booking.setBooker(booker);

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
    }

    @Test
    void testCreateBookingRequestWhenBookingIsCreatedThenReturnBookingResponseDto() {
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        when(bookingMapper.bookingFromDto(bookingRequestDto, booker, item)).thenReturn(booking);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.createBookingRequest(bookingRequestDto, bookerId);

        assertNotNull(result);
        assertEquals(bookingResponseDto.getId(), result.getId());
        verify(bookingRepository, times(1)).save(booking);
    }

    @Test
    void testCreateBookingRequestWhenUserNotExistsThenThrowUserNotExistsException() {
        when(userStorage.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class, () -> bookingService.createBookingRequest(bookingRequestDto, bookerId));
    }

    @Test
    void testCreateBookingRequestWhenItemNotExistsThenThrowItemNotExistsException() {
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotExistsException.class, () -> bookingService.createBookingRequest(bookingRequestDto, bookerId));
    }

    @Test
    void testCreateBookingRequestWhenItemNotAvailableThenThrowItemNotAvailableException() {
        item.setAvailable(false);
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class, () -> bookingService.createBookingRequest(bookingRequestDto, bookerId));
    }

    @Test
    void testCreateBookingRequestWhenItemOwnerIsBookerThenThrowIllegalAccessForUserException() {
        item.setOwner(booker);
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));

        assertThrows(IllegalAccessForUserException.class, () -> bookingService.createBookingRequest(bookingRequestDto, bookerId));
    }

    @Test
    void updateBookingWhenBookingIsUpdatedThenReturnBookingResponseDto() {
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(booking);
        booking.setBookingStatus(BookingStatus.WAITING);
        when(bookingRepository.save(booking)).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto result = bookingService.updateBooking(booking.getId(), item.getOwner().getId(), true);

        assertNotNull(result, "Результат не должен быть null");
        assertEquals(bookingResponseDto.getId(), result.getId(), "Идентификаторы должны совпадать");


        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void updateBookingWhenUserNotOwnerThenThrowException() {
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(booking);
        booking.setBookingStatus(BookingStatus.WAITING);

        assertThrows(IllegalAccessForUserException.class, () -> bookingService.updateBooking(booking.getId(), 999, true));
    }

    @Test
    void updateBookingWhenNewStatusEqualsThenThrowException() {
        booking.setBookingStatus(BookingStatus.APPROVED);
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(booking);

        assertThrows(ValidationException.class, () -> bookingService.updateBooking(booking.getId(), item.getOwner().getId(), true));
    }

    @Test
    void getBookingByIdWhenFindBookingThenReturnBookingResponseDto() {
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(booking);
        when(bookingMapper.toBookingResponseDto(booking)).thenReturn(bookingResponseDto);

        BookingResponseDto bookingById = bookingService.getBookingById(booking.getId(), bookerId);

        assertThat(bookingById).isNotNull();
        assertThat(bookingById.getId()).isEqualTo(booking.getId());
        verify(bookingRepository, times(1)).findBookingByIdWithItemAndBookerEagerly(any());
    }

    @Test
    void getBookingByIdWhenBookingIsEmptyThenThrowException() {
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(null);

        assertThrows(BookingNotExistsException.class, () -> bookingService.getBookingById(booking.getId(), bookerId));
    }

    @Test
    void getBookingByIdWhenUserNotOwnerThenThrowException() {
        booking.getBooker().setId(999);
        when(bookingRepository.findBookingByIdWithItemAndBookerEagerly(booking.getId())).thenReturn(booking);

        assertThrows(IllegalAccessForUserException.class, () -> bookingService.getBookingById(booking.getId(), bookerId));
    }

    @Test
    void getBookingByUserWhenStateIsAllThenReturnSortedBookings() {
        String state;
        Integer userId = 1;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Slice<Booking> bookingsSlice = Mockito.mock(Slice.class);
        List<BookingResponseDto> expectedResponse = new ArrayList<>();
        when(userStorage.findById(eq(userId))).thenReturn(Optional.of(new User()));
        when(bookingRepository.findAllBookingsWithItemAndBookerSortedByStartDateDesc(eq(userId), eq(pageable)))
                .thenReturn(bookingsSlice);
        when(bookingRepository.findAllCurrentBookingsWithItemAndBooker(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findAllPastBookingsWithItemAndBooker(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findAllFutureBookingsWithItemAndBooker(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findWaitingBookingsSortedByStartDateDesc(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findRejectedBookingsSortedByStartDateDesc(eq(userId), eq(pageable))).thenReturn(bookingsSlice);

        when(bookingMapper.toBookingDtoList(bookingsSlice.getContent())).thenReturn(expectedResponse);

        state = "ALL";
        List<BookingResponseDto> result = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        state = "CURRENT";
        List<BookingResponseDto> resultCurrent = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(resultCurrent);
        assertEquals(expectedResponse, resultCurrent);

        state = "PAST";
        List<BookingResponseDto> resultPast = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(resultPast);
        assertEquals(expectedResponse, resultPast);

        state = "FUTURE";
        List<BookingResponseDto> resultFuture = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(resultFuture);
        assertEquals(expectedResponse, resultFuture);

        state = "WAITING";
        List<BookingResponseDto> resultWaiting = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(resultWaiting);
        assertEquals(expectedResponse, resultWaiting);

        state = "REJECTED";
        List<BookingResponseDto> resultRejected = bookingService.getBookingByUser(state, userId, from, size);
        assertNotNull(resultRejected);
        assertEquals(expectedResponse, resultRejected);
    }

    @Test
    void getBookingByUserWhenGivenUnsupportedStateThenThrowException() {
        String state = "UNSUPPORTED";
        Integer from = 0;
        Integer size = 10;

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getBookingByUser(state, bookerId, from, size));
    }

    @Test
    void getBookingByUserWhenGivenNotExistUserIdThenThrowException() {
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        assertThrows(UserNotExistsException.class, () -> bookingService.getBookingByUser(state, 999, from, size));
    }

    @Test
    void getBookingByOwnerWhenStateIsAllThenReturnSortedBookings() {
        String state;
        Integer userId = 1;
        Integer from = 0;
        Integer size = 10;
        Pageable pageable = PageRequest.of(from / size, size);
        Slice<Booking> bookingsSlice = Mockito.mock(Slice.class);
        List<BookingResponseDto> expectedResponse = new ArrayList<>();
        when(userStorage.findById(eq(userId))).thenReturn(Optional.of(new User()));
        when(bookingRepository.findByItem_Owner_IdOrderByStartDesc(eq(userId), eq(pageable)))
                .thenReturn(bookingsSlice);
        when(bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(
                eq(userId), any(LocalDateTime.class), any(LocalDateTime.class), eq(pageable)))
                .thenReturn(bookingsSlice);
        when(bookingRepository.findAllPastBookingsWithItemAndBooker(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findAllFutureBookingsWithItemAndBooker(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findWaitingBookingsSortedByStartDateDesc(eq(pageable))).thenReturn(bookingsSlice);
        when(bookingRepository.findRejectedBookingsByOwnerSortedByStartDateDesc(eq(userId), eq(pageable))).thenReturn(bookingsSlice);

        when(bookingMapper.toBookingDtoList(bookingsSlice.getContent())).thenReturn(expectedResponse);

        state = "ALL";
        List<BookingResponseDto> result = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(result);
        assertEquals(expectedResponse, result);

        state = "CURRENT";
        List<BookingResponseDto> resultCurrent = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(resultCurrent);
        assertEquals(expectedResponse, resultCurrent);

        state = "PAST";
        List<BookingResponseDto> resultPast = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(resultPast);
        assertEquals(expectedResponse, resultPast);

        state = "FUTURE";
        List<BookingResponseDto> resultFuture = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(resultFuture);
        assertEquals(expectedResponse, resultFuture);

        state = "WAITING";
        List<BookingResponseDto> resultWaiting = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(resultWaiting);
        assertEquals(expectedResponse, resultWaiting);

        state = "REJECTED";
        List<BookingResponseDto> resultRejected = bookingService.getBookingByOwner(state, userId, from, size);
        assertNotNull(resultRejected);
        assertEquals(expectedResponse, resultRejected);
    }

    @Test
    void getBookingByOwnerWhenGivenUnsupportedStateThenThrowException() {
        String state = "UNSUPPORTED";
        Integer from = 0;
        Integer size = 10;

        assertThrows(UnsupportedStatusException.class, () -> bookingService.getBookingByOwner(state, bookerId, from, size));
    }

    @Test
    void getBookingByOwnerWhenGivenNotExistUserIdThenThrowException() {
        String state = "ALL";
        Integer from = 0;
        Integer size = 10;

        assertThrows(UserNotExistsException.class, () -> bookingService.getBookingByOwner(state, 999, from, size));
    }
}
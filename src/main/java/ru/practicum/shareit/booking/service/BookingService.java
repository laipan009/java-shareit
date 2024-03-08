package ru.practicum.shareit.booking.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.LockModeType;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserStorage userStorage, ItemStorage itemStorage, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.bookingMapper = bookingMapper;
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    public BookingResponseDto createBookingRequest(BookingRequestDto bookingDto, Integer bookerId) {
        log.info("Attempt to create booking by user with ID = {} to item by ID = {}", bookerId, bookingDto.getItemId());
        User booker = userStorage.findById(bookerId)
                .orElseThrow((() -> new UserNotExistsException("User not exists")));
        Item bookedItem = itemStorage.findById(bookingDto.getItemId())
                .orElseThrow((() -> new ItemNotExistsException("Item not exists")));

        if (Objects.equals(bookedItem.getOwner().getId(), bookerId)) {
            throw new IllegalAccessForUserException("Item owner is not be booker");
        }
        if (!bookedItem.getAvailable()) {
            throw new ItemNotAvailableException("Item status is not available");
        }
        Booking bookingAfterMap = bookingMapper.bookingFromDto(bookingDto, booker, bookedItem);
        Booking savedBooking = bookingRepository.save(bookingAfterMap);
        return bookingMapper.toBookingResponseDto(savedBooking);
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    public BookingResponseDto updateBooking(Integer bookingId, Integer ownerId, Boolean approved) {
        Booking booking = bookingRepository.findBookingByIdWithItemAndBookerEagerly(bookingId);
        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new IllegalAccessForUserException("Only the owner can update the booking status");
        }
        BookingStatus newStatus = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        if (booking.getBookingStatus().equals(newStatus)) {
            throw new ValidationException("Booking status in the request is equal to the status in the database");
        }
        booking.setBookingStatus(newStatus);
        bookingRepository.save(booking);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findBookingByIdWithItemAndBookerEagerly(bookingId);
        if (Optional.ofNullable(booking).isEmpty()) {
            throw new BookingNotExistsException("Booking not exist");
        }
        if (Objects.equals(booking.getItem().getOwner().getId(), userId)
                || Objects.equals(booking.getBooker().getId(), userId)) {
            return bookingMapper.toBookingResponseDto(booking);
        } else {
            throw new IllegalAccessForUserException("Only the owner can browse the booking status");
        }
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    public List<BookingResponseDto> getBookingByUser(String state, Integer userId, Integer from, Integer size) {
        int pageNumber = from / size;
        Pageable page = PageRequest.of(pageNumber, size);

        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }
        userStorage.findById(userId)
                .orElseThrow((() -> new UserNotExistsException("User not exists with id: " + userId)));

        switch (bookingState) {
            case ALL:
                Slice<Booking> sortedBookings = bookingRepository.findAllBookingsWithItemAndBookerSortedByStartDateDesc(userId, page);
                return bookingMapper.toBookingDtoList(sortedBookings.getContent());
            case CURRENT:
                Slice<Booking> currentBookings = bookingRepository.findAllCurrentBookingsWithItemAndBooker(page);
                return bookingMapper.toBookingDtoList(currentBookings.getContent());
            case PAST:
                Slice<Booking> pastBookings = bookingRepository.findAllPastBookingsWithItemAndBooker(page);
                return bookingMapper.toBookingDtoList(pastBookings.getContent());
            case FUTURE:
                Slice<Booking> futureBookings = bookingRepository.findAllFutureBookingsWithItemAndBooker(page);
                return bookingMapper.toBookingDtoList(futureBookings.getContent());
            case WAITING:
                Slice<Booking> waitingBookings = bookingRepository.findWaitingBookingsSortedByStartDateDesc(page);
                return bookingMapper.toBookingDtoList(waitingBookings.getContent());
            case REJECTED:
                Slice<Booking> rejectedBookings = bookingRepository.findRejectedBookingsSortedByStartDateDesc(userId, page);
                return bookingMapper.toBookingDtoList(rejectedBookings.getContent());
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    @Transactional
    @Lock(value = LockModeType.PESSIMISTIC_READ)
    public List<BookingResponseDto> getBookingByOwner(String state, Integer ownerId, Integer from, Integer size) {
        Pageable page = PageRequest.of(from, size);
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: " + state);
        }

        userStorage.findById(ownerId)
                .orElseThrow((() -> new UserNotExistsException("User not exists with id: " + ownerId)));

        LocalDateTime now = LocalDateTime.now();
        switch (bookingState) {
            case ALL:
                Slice<Booking> orderedBookings = bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId, page);
                return bookingMapper.toBookingDtoList(orderedBookings.getContent());
            case CURRENT:
                Slice<Booking> bookings = bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now, page);
                return bookingMapper.toBookingDtoList(bookings.getContent());
            case PAST:
                Slice<Booking> pastBookings = bookingRepository.findAllPastBookingsWithItemAndBooker(page);
                return bookingMapper.toBookingDtoList(pastBookings.getContent());
            case FUTURE:
                Slice<Booking> futureBookings = bookingRepository.findAllFutureBookingsWithItemAndBooker(page);
                return bookingMapper.toBookingDtoList(futureBookings.getContent());
            case WAITING:
                Slice<Booking> waitingBookings = bookingRepository.findWaitingBookingsSortedByStartDateDesc(page);
                return bookingMapper.toBookingDtoList(waitingBookings.getContent());
            case REJECTED:
                Slice<Booking> rejectedBookings = bookingRepository.findRejectedBookingsByOwnerSortedByStartDateDesc(ownerId, page);
                return bookingMapper.toBookingDtoList(rejectedBookings.getContent());
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }
}
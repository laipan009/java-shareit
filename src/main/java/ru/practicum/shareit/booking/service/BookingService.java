package ru.practicum.shareit.booking.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public BookingService(BookingRepository bookingRepository, UserStorage userStorage, ItemStorage itemStorage) {
        this.bookingRepository = bookingRepository;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    public BookingResponseDto createBookingRequest(BookingRequestDto bookingDto, Integer bookerId) {
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
        Booking bookingAfterMap = BookingMapper.bookingFromDto(bookingDto, booker, bookedItem);
        Booking savedBooking = bookingRepository.save(bookingAfterMap);
        return BookingMapper.toBookingResponseDto(savedBooking);
    }

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
        return BookingMapper.toBookingResponseDto(booking);
    }

    public BookingResponseDto getBookingById(Integer bookingId, Integer userId) {
        Booking booking = bookingRepository.findBookingByIdWithItemAndBookerEagerly(bookingId);
        if (Optional.ofNullable(booking).isEmpty()) {
            throw new BookingNotExistsException("Booking not exist");
        }
        if (Objects.equals(booking.getItem().getOwner().getId(), userId)
                || Objects.equals(booking.getBooker().getId(), userId)) {
            return BookingMapper.toBookingResponseDto(booking);
        } else {
            throw new IllegalAccessForUserException("Only the owner can browse the booking status");
        }
    }

    public List<BookingResponseDto> getBookingByUser(String state, Integer userId) {
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
                return BookingMapper.toBookingDtoList(bookingRepository.findAllBookingsWithItemAndBookerSortedByStartDateDesc(userId));
            case CURRENT:
                return BookingMapper.toBookingDtoList(bookingRepository.findAllCurrentBookingsWithItemAndBooker());
            case PAST:
                return BookingMapper.toBookingDtoList(bookingRepository.findAllPastBookingsWithItemAndBooker());
            case FUTURE:
                return BookingMapper.toBookingDtoList(bookingRepository.findAllFutureBookingsWithItemAndBooker());
            case WAITING:
                return BookingMapper.toBookingDtoList(bookingRepository.findWaitingBookingsSortedByStartDateDesc());
            case REJECTED:
                return BookingMapper.toBookingDtoList(bookingRepository.findRejectedBookingsSortedByStartDateDesc(userId));
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }

    public List<BookingResponseDto> getBookingByOwner(String state, Integer ownerId) {
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
                return BookingMapper.toBookingDtoList(bookingRepository.findByItem_Owner_IdOrderByStartDesc(ownerId));
            case CURRENT:
                return BookingMapper.toBookingDtoList(
                        bookingRepository.findByItem_Owner_IdAndStartBeforeAndEndAfterOrderByStartDesc(ownerId, now, now));
            case PAST:
                return BookingMapper.toBookingDtoList(bookingRepository.findAllPastBookingsWithItemAndBooker());
            case FUTURE:
                return BookingMapper.toBookingDtoList(bookingRepository.findAllFutureBookingsWithItemAndBooker());
            case WAITING:
                return BookingMapper.toBookingDtoList(bookingRepository.findWaitingBookingsSortedByStartDateDesc());
            case REJECTED:
                return BookingMapper.toBookingDtoList(bookingRepository.findRejectedBookingsByOwnerSortedByStartDateDesc(ownerId));
            default:
                throw new UnsupportedStatusException("Unknown state: " + state);
        }
    }
}
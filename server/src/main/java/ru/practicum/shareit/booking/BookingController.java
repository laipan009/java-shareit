package ru.practicum.shareit.booking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.EndTimeBeforeStartException;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {

    private final BookingService bookingService;

    private static final String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingResponseDto createBookingRequest(@Valid @RequestBody BookingRequestDto bookingRequestDto,
                                                   @RequestHeader(HEADER_WITH_USER_ID) Integer bookerId) {
        checkEndTimeBeforeStart(bookingRequestDto);
        return bookingService.createBookingRequest(bookingRequestDto, bookerId);
    }

    @PatchMapping("{bookingId}")
    public BookingResponseDto updateBooking(@PathVariable Integer bookingId,
                                            @RequestHeader(HEADER_WITH_USER_ID) Integer ownerId,
                                            @RequestParam(value = "approved") Boolean approved) {
        return bookingService.updateBooking(bookingId, ownerId, approved);
    }

    @GetMapping("{bookingId}")
    public BookingResponseDto getBookingById(@PathVariable Integer bookingId,
                                             @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getBookingByUser(
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(value = 0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(value = 0) Integer size,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        return bookingService.getBookingByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getBookingByOwner(
            @RequestParam(value = "from", defaultValue = "0", required = false) @Min(value = 0) Integer from,
            @RequestParam(value = "size", defaultValue = "10", required = false) @Min(value = 0) Integer size,
            @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
            @RequestHeader(HEADER_WITH_USER_ID) Integer ownerId) {
        return bookingService.getBookingByOwner(state, ownerId, from, size);
    }

    private void checkEndTimeBeforeStart(BookingRequestDto bookingRequestDto) {
        if (bookingRequestDto.getEnd().isBefore(bookingRequestDto.getStart())
                || bookingRequestDto.getEnd().isEqual(bookingRequestDto.getStart())) {
            throw new EndTimeBeforeStartException("End date must be after start date");
        }
    }
}
package ru.practicum.shareit.booking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@FeignClient(name = "booking-server", url = "http://server:8090/bookings")
public interface BookingFeignClient {

    String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    BookingResponseDto createBookingRequest(@RequestBody BookingRequestDto bookingRequestDto,
                                            @RequestHeader(HEADER_WITH_USER_ID) Integer bookerId);

    @PatchMapping("/{bookingId}")
    BookingResponseDto updateBooking(@PathVariable("bookingId") Integer bookingId,
                                     @RequestHeader(HEADER_WITH_USER_ID) Integer ownerId,
                                     @RequestParam("approved") Boolean approved);

    @GetMapping("/{bookingId}")
    BookingResponseDto getBookingById(@PathVariable("bookingId") Integer bookingId,
                                      @RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping
    List<BookingResponseDto> getBookingByUser(@RequestParam("state") String state,
                                              @RequestHeader(HEADER_WITH_USER_ID) Integer userId,
                                              @RequestParam("from") Integer from,
                                              @RequestParam("size") Integer size);

    @GetMapping("/owner")
    List<BookingResponseDto> getBookingByOwner(@RequestParam("state") String state,
                                               @RequestHeader(HEADER_WITH_USER_ID) Integer userId,
                                               @RequestParam("from") Integer from,
                                               @RequestParam("size") Integer size);
}
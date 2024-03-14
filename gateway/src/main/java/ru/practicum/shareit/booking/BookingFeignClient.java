package ru.practicum.shareit.booking;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;

import java.util.List;

@FeignClient(name = "shareIt-server", url = "http://localhost:8080/bookings")
public interface BookingFeignClient {

    @PostMapping
    BookingResponseDto createBookingRequest(BookingRequestDto bookingRequestDto,
                                            Integer bookerId);

    @PatchMapping("{bookingId}")
    BookingResponseDto updateBooking(Integer bookingId,
                                     Integer ownerId,
                                     Boolean approved);


    @GetMapping("{bookingId}")
    BookingResponseDto getBookingById(Integer bookingId,
                                      Integer userId);


    @GetMapping
    List<BookingResponseDto> getBookingByUser(
            String state,
            Integer userId,
            Integer from,
            Integer size);


    @GetMapping("/owner")
    List<BookingResponseDto> getBookingByOwner(
            String state,
            Integer userId,
            Integer from,
            Integer size);

}

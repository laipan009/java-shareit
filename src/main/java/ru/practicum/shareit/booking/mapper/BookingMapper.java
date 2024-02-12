package ru.practicum.shareit.booking.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class BookingMapper {
    public static BookingResponseDto toBookingResponseDto(Booking booking) {
        log.info("Attempt to map booking with id {} to BookingDto", booking.getId());
        return BookingResponseDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(booking.getBooker())
                .item(booking.getItem())
                .status(booking.getBookingStatus())
                .build();
    }

    public static Booking bookingFromDto(BookingRequestDto bookingRequestDto, User booker, Item item) {
        log.info("Attempt to map booking with item id {} to Booking", bookingRequestDto.getItemId());
        return Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .bookingStatus(BookingStatus.WAITING)
                .build();
    }

    public static List<BookingResponseDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream()
                .map(BookingMapper::toBookingResponseDto)
                .collect(Collectors.toList());
    }

    public static ShortBookingDto toBookingItemDto(Booking booking) {
        return ShortBookingDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }
}
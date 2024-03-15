package ru.practicum.shareit.booking.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, ItemMapper.class})
public interface BookingMapper {

    @Mappings({
            @Mapping(target = "status", source = "booking.bookingStatus"),
            @Mapping(target = "id", source = "booking.id"),
            @Mapping(target = "start", source = "booking.start"),
            @Mapping(target = "end", source = "booking.end"),
            @Mapping(target = "booker", source = "booking.booker"),
            @Mapping(target = "item", source = "booking.item")
    })
    BookingResponseDto toBookingResponseDto(Booking booking);

    @Mappings({
            @Mapping(target = "bookingStatus", constant = "WAITING"),
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "start", source = "bookingRequestDto.start"),
            @Mapping(target = "end", source = "bookingRequestDto.end"),
            @Mapping(target = "booker", source = "booker"),
            @Mapping(target = "item", source = "item")
    })
    Booking bookingFromDto(BookingRequestDto bookingRequestDto, User booker, Item item);

    List<BookingResponseDto> toBookingDtoList(List<Booking> bookings);

    @Mapping(target = "bookerId", source = "booker.id")
    ShortBookingDto toShortBookingDto(Booking booking);
}
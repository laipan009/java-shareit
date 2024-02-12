package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.Optional;

@Slf4j
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        log.info("Attempt to map item with id {} to ItemDto", item.getId());
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public static Item updateItemFromDto(Item existingItem, ItemDto itemDto) {
        log.info("Attempt to update some attributes item with id {}", existingItem.getId());
        Optional.ofNullable(itemDto.getId()).ifPresent(existingItem::setId);
        Optional.ofNullable(itemDto.getName()).ifPresent(existingItem::setName);
        Optional.ofNullable(itemDto.getDescription()).ifPresent(existingItem::setDescription);
        Optional.ofNullable(itemDto.getAvailable()).ifPresent(existingItem::setAvailable);
        return existingItem;
    }

    public static Item getItemFromDto(ItemDto itemDto, User owner) {
        log.info("Attempt to map itemDto with id {} to item", itemDto.getId());
        return Item.builder()
                .owner(owner)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemDtoForOwner toItemBookingDto(Item item,
                                                   ShortBookingDto last,
                                                   ShortBookingDto next,
                                                   List<CommentOutputDto> comments) {

        log.info("Attempt to map item with id {} to ItemDtoForOwner", item.getId());
        return ItemDtoForOwner.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.getAvailable())
                .description(item.getDescription())
                .lastBooking(last)
                .nextBooking(next)
                .comments(comments)
                .build();
    }
}
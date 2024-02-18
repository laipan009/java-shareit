
package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.dto.CommentOutputDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    Item updateItemFromDto(@MappingTarget Item existingItem, ItemDto itemDto);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "owner", source = "owner"),
            @Mapping(target = "name", source = "itemDto.name"),
            @Mapping(target = "description", source = "itemDto.description"),
            @Mapping(target = "available", source = "itemDto.available")
    })
    Item getItemFromDto(ItemDto itemDto, User owner);

    @Mappings({
            @Mapping(target = "id", source = "item.id"),
            @Mapping(target = "name", source = "item.name"),
            @Mapping(target = "description", source = "item.description"),
            @Mapping(target = "available", source = "item.available"),
            @Mapping(target = "lastBooking", source = "lastBooking"),
            @Mapping(target = "nextBooking", source = "nextBooking"),
            @Mapping(target = "comments", source = "comments")
    })
    ItemDtoForOwner toItemBookingDto(Item item,
                                     ShortBookingDto lastBooking,
                                     ShortBookingDto nextBooking,
                                     List<CommentOutputDto> comments);
}
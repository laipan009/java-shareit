package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

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
                .request(item.getRequest() != null ? item.getRequest().getId() : null)
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

    public static Item getItemFromItemDto(ItemDto itemDto, int ownerId) {
        log.info("Attempt to map itemDto with id {} to item", itemDto.getId());
        return Item.builder()
                .owner(ownerId)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
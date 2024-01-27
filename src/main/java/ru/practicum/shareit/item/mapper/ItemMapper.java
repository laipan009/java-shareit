package ru.practicum.shareit.item.mapper;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Slf4j
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        log.info("Attempt to map item with id {} to ItemDto", item.getId());
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

    public static Item updateItemFromDto(Item existingItem, ItemDto itemDto) {
        log.info("Attempt to update some attributes item with id {}", existingItem.getId());
        if (itemDto.getId() != null) {
            existingItem.setId(itemDto.getId());
        }
        if (itemDto.getName() != null) {
            existingItem.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            existingItem.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            existingItem.setAvailable(itemDto.getAvailable());
        }
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
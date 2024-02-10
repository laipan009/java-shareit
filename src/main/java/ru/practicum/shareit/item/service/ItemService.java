package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.UserNotExistsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.service.UserService;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserService userService) {
        this.itemStorage = itemStorage;
        this.userService = userService;
    }

    public ItemDto addItem(ItemDto itemDto, int userId) {
        log.info("Attempt to add new item by user with id {}", userId);
        if (!userService.isUserExists(userId)) {
            throw new UserNotExistsException("User with same id not exists");
        }
        Item mappedItem = ItemMapper.getItemFromItemDto(itemDto, userId);
        return ItemMapper.toItemDto(itemStorage.createItem(mappedItem));
    }

    public ItemDto updateItem(int itemId, ItemDto itemDto, int userId) {
        log.info("Attempt to update item by id {} for user with id {}", itemId, userId);
        Item itemById = itemStorage.getItemById(itemId);
        if (!itemById.getOwner().equals(userId)) {
            throw new NotOwnerException("This user is not owner for this item");
        }
        Item updatedItem = ItemMapper.updateItemFromDto(itemById, itemDto);
        itemStorage.updateItem(updatedItem);
        return ItemMapper.toItemDto(updatedItem);
    }

    public ItemDto getItemById(int itemId) {
        log.info("Attempt to received item by id {}", itemId);
        Item item = itemStorage.getItemById(itemId);
        return ItemMapper.toItemDto(item);
    }

    public List<ItemDto> getItemsByUserId(int userId) {
        log.info("Attempt to received items for user with id {}", userId);
        if (!userService.isUserExists(userId)) {
            throw new UserNotExistsException("User with same id not exists");
        }
        return itemStorage.getItemsByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Attempt to search items by key-word {}", text);

        return itemStorage.getAllItems().stream()
                .filter(Item::getAvailable)
                .filter(item -> StringUtils.containsIgnoreCase(item.getName(), text)
                        || StringUtils.containsIgnoreCase(item.getDescription(), text))
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
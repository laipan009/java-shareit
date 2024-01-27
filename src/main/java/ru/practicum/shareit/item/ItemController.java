package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                           @RequestHeader(value = "X-Sharer-User-Id") int userId) {
        log.info("Received POST request from user {}", userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @Validated(OnUpdate.class) @RequestBody ItemDto itemDto,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        if (userId == null) {
            throw new NotOwnerException("Request not has User Id");
        }
        log.info("Received PATCH request from user {}", userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId) {
        log.info("Received GET request for get item by id {}", itemId);
        return itemService.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByUserId(@RequestHeader("X-Sharer-User-Id") int userId) {
        log.info("Received GET request for get items for user by id {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Received GET request for search items by word - {}", text);
        return itemService.searchItems(text);
    }
}
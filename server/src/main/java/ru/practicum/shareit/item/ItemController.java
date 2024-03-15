package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private static final String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader(HEADER_WITH_USER_ID) int userId) {
        log.info("Received POST request from user {}", userId);
        return itemService.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @RequestBody ItemDto itemDto,
                              @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        log.info("Received PATCH request from user {}", userId);
        return itemService.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoForOwner getItemById(@PathVariable int itemId, @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        log.info("Received GET request for get item by id {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoForOwner> getItemsByUserId(@RequestHeader(HEADER_WITH_USER_ID) int userId) {
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

    @PostMapping("/{itemId}/comment")
    public CommentOutputDto saveComment(@RequestBody CommentInputDto comment,
                                        @PathVariable Integer itemId,
                                        @RequestHeader(HEADER_WITH_USER_ID) Integer authorId) {
        return itemService.saveComment(comment, itemId, authorId);
    }
}
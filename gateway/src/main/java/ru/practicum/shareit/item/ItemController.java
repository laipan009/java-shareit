package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemFeignClient feignClient;
    private static final String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    @Autowired
    public ItemController(ItemFeignClient feignClient) {
        this.feignClient = feignClient;
    }

    @PostMapping
    public ItemDto addItem(@Validated(OnCreate.class) @RequestBody ItemDto itemDto,
                           @RequestHeader(HEADER_WITH_USER_ID) int userId) {
        log.info("Received POST request from user {}", userId);
        return feignClient.addItem(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable int itemId,
                              @Validated(OnUpdate.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        if (userId == null) {
            throw new NotOwnerException("Request not has User Id");
        }
        log.info("Received PATCH request from user {}", userId);
        return feignClient.updateItem(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable int itemId, @RequestHeader(HEADER_WITH_USER_ID) Integer userId) {
        log.info("Received GET request for get item by id {}", itemId);
        return feignClient.getItemById(itemId, userId);
    }

    @GetMapping
    public List<ItemDtoForOwner> getItemsByUserId(@RequestHeader(HEADER_WITH_USER_ID) int userId) {
        log.info("Received GET request for get items for user by id {}", userId);
        return feignClient.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam(value = "text") String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        log.info("Received GET request for search items by word - {}", text);
        return feignClient.searchItems(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentOutputDto saveComment(@RequestBody CommentInputDto comment,
                                        @PathVariable Integer itemId,
                                        @RequestHeader(HEADER_WITH_USER_ID) Integer authorId) {
        return feignClient.saveComment(comment, itemId, authorId);
    }
}

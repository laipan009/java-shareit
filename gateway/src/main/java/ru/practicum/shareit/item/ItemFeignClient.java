package ru.practicum.shareit.item;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;

import java.util.List;

@FeignClient(name = "item-server", url = "http://server:8090/items")
public interface ItemFeignClient {

    String HEADER_WITH_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    ItemDto addItem(@RequestBody ItemDto itemDto,
                    @RequestHeader(HEADER_WITH_USER_ID) int userId);

    @PatchMapping("/{itemId}")
    ItemDto updateItem(@PathVariable int itemId,
                       @RequestBody ItemDto itemDto,
                       @RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping("/{itemId}")
    ItemDtoForOwner getItemById(@PathVariable int itemId, @RequestHeader(HEADER_WITH_USER_ID) Integer userId);

    @GetMapping
    List<ItemDtoForOwner> getItemsByUserId(@RequestHeader(HEADER_WITH_USER_ID) int userId);

    @GetMapping("/search")
    List<ItemDto> searchItems(@RequestParam(value = "text") String text);

    @PostMapping("/{itemId}/comment")
    CommentOutputDto saveComment(@RequestBody CommentInputDto comment,
                                 @PathVariable Integer itemId,
                                 @RequestHeader(HEADER_WITH_USER_ID) Integer authorId);
}
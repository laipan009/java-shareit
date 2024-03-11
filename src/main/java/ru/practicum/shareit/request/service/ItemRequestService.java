package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.RequestNotExistsException;
import ru.practicum.shareit.exception.UserNotExistsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemRequestService {

    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestStorage itemRequestStorage;
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;
    private final ItemMapper itemMapper;

    @Autowired
    public ItemRequestService(ItemRequestMapper itemRequestMapper, ItemRequestStorage itemRequestStorage, UserStorage userStorage, ItemStorage itemStorage, ItemMapper itemMapper) {
        this.itemRequestMapper = itemRequestMapper;
        this.itemRequestStorage = itemRequestStorage;
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
        this.itemMapper = itemMapper;
    }

    @Transactional()
    public ItemRequestDto createItemRequest(ItemRequestDto itemRequestDto, Integer userId) {
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotExistsException("User with same id not exists"));
        ItemRequest itemRequestFromDto = itemRequestMapper.toItemRequestFromDto(itemRequestDto, user);
        ItemRequest savedRequest = itemRequestStorage.save(itemRequestFromDto);
        return itemRequestMapper.toDtoFromItemRequest(savedRequest);
    }

    @Transactional(readOnly = true)
    public List<RequestDtoResponse> getRequestsForRequester(Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotExistsException("User with id " + userId + " does not exist.");
        }

        List<Item> items = itemStorage.findByRequest_Requestor_Id(userId);

        List<ItemRequest> requests = itemRequestStorage.findByRequestor_Id(userId);

        Map<Integer, List<ItemDto>> itemsByRequestId = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        return requests.stream()
                .map(itemRequestMapper::toRequestDtoResponse)
                .peek(dto -> dto.setItems(itemsByRequestId.getOrDefault(dto.getId(), Collections.emptyList())))
                .sorted(Comparator.comparing(RequestDtoResponse::getCreated))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RequestDtoResponse getRequestById(Integer requestId, Integer userId) {
        if (!userStorage.existsById(userId)) {
            throw new UserNotExistsException("User with id " + userId + " does not exist.");
        }
        ItemRequest request = itemRequestStorage.findById(requestId)
                .orElseThrow(() -> new RequestNotExistsException("Request with id " + requestId + " does not exist."));
        RequestDtoResponse requestDtoResponse = itemRequestMapper.toRequestDtoResponse(request);
        Item itemByRequestId = itemStorage.findItemByRequestId(requestId);
        List<ItemDto> item = itemByRequestId == null ? Collections.emptyList() : List.of(itemMapper.toItemDto(itemByRequestId));
        requestDtoResponse.setItems(item);
        return requestDtoResponse;
    }

    @Transactional(readOnly = true)
    public List<RequestDtoResponse> getRequests(Integer from, Integer size, Integer userId) {
        Pageable pageable = PageRequest.of(from, size);
        Slice<ItemRequest> slice = itemRequestStorage.findAllItemRequestsSortedByCreatedDesc(pageable, userId);

        List<Integer> requestIds = slice.getContent().stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList());

        List<Item> items = itemStorage.findItemsByRequestIds(requestIds);

        Map<Integer, List<ItemDto>> itemsByRequestId = items.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.groupingBy(ItemDto::getRequestId));

        List<RequestDtoResponse> result = slice.getContent().stream()
                .map(itemRequestMapper::toRequestDtoResponse)
                .peek(dto -> dto.setItems(itemsByRequestId.getOrDefault(dto.getId(), Collections.emptyList())))
                .collect(Collectors.toList());

        return result;
    }
}
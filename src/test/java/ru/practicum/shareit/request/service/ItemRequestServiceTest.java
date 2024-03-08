package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.RequestNotExistsException;
import ru.practicum.shareit.exception.UserNotExistsException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceTest {

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private ItemRequestMapper itemRequestMapper;

    @Mock
    private ItemRequestStorage itemRequestStorage;

    @InjectMocks
    private ItemRequestService itemRequestService;


    private BookingRequestDto bookingRequestDto;
    private User booker;
    private User owner;
    private Item item;
    private ItemDto itemDtoRequest;
    private ItemDto itemDtoResponse;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private ItemRequest request;
    private ItemRequestDto itemRequestDto;
    private RequestDtoResponse requestDtoResponse;
    private Comment comment;
    private CommentOutputDto commentOutputDto;
    private final Integer bookerId = 1;
    private final Integer ownerId = 2;
    private final Integer itemId = 1;
    private final Integer requestId = 1;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(bookerId);

        owner = new User();
        owner.setId(ownerId);

        bookingRequestDto = BookingRequestDto.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .itemId(itemId)
                .build();

        itemDtoRequest = ItemDto.builder()
                .name("Пылесос профессиональный")
                .description("Предназначен для влажной химчистки ковров, диванов, салона авто")
                .requestId(requestId)
                .available(true)
                .build();

        item = Item.builder()
                .id(itemId)
                .name(itemDtoRequest.getName())
                .description(itemDtoRequest.getDescription())
                .available(itemDtoRequest.getAvailable())
                .owner(owner)
                .build();

        comment = Comment.builder()
                .text("Хороший пылесос")
                .author(booker)
                .item(item)
                .build();

        commentOutputDto = CommentOutputDto.builder()
                .text(comment.getText())
                .created(LocalDateTime.now())
                .build();

        itemDtoResponse = ItemDto.builder()
                .id(itemId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();

        request = ItemRequest.builder()
                .id(requestId)
                .description("Есть у кого пылесос?")
                .created(LocalDateTime.now())
                .requestor(booker)
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(requestId)
                .description(request.getDescription())
                .created(request.getCreated())
                .build();

        requestDtoResponse = RequestDtoResponse.builder()
                .id(requestId)
                .description(request.getDescription())
                .created(request.getCreated())
                .items(List.of(itemDtoResponse))
                .build();

        booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();


        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
    }

    @Test
    void createItemRequestWhenRequestIsCreatedThenReturnItemRequestDto() {
        when(userStorage.findById(anyInt())).thenReturn(Optional.of(booker));
        when(itemRequestMapper.toItemRequestFromDto(any(ItemRequestDto.class), any(User.class))).thenReturn(request);
        when(itemRequestStorage.save(any(ItemRequest.class))).thenReturn(request);
        when(itemRequestMapper.toDtoFromItemRequest(any(ItemRequest.class))).thenReturn(itemRequestDto);

        ItemRequestDto itemRequest = itemRequestService.createItemRequest(itemRequestDto, bookerId);

        assertNotNull(itemRequest);
        assertEquals(itemRequestDto.getId(), itemRequest.getId());
        verify(itemRequestStorage, times(1)).save(request);
    }

    @Test
    void createItemRequestWhenUserNotExistThenThrowException() {
        when(userStorage.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class, () -> itemRequestService.createItemRequest(itemRequestDto, bookerId));
    }

    @Test
    void getRequestsForRequesterWhenUserExistThenThrowException() {
        when(userStorage.existsById(anyInt())).thenReturn(false);

        assertThrows(UserNotExistsException.class, () -> itemRequestService.getRequestsForRequester(999));
    }

    @Test
    void getRequestsForRequesterWhenUserExistsThenReturnListRequestDtoResponse() {
        when(userStorage.existsById(anyInt())).thenReturn(true);
        when(itemStorage.findByRequest_Requestor_Id(bookerId)).thenReturn(List.of(item));
        when(itemRequestStorage.findByRequestor_Id(bookerId)).thenReturn(List.of(request));
        when(itemMapper.toItemDto(item)).thenReturn(itemDtoResponse);
        when(itemRequestMapper.toRequestDtoResponse(request)).thenReturn(requestDtoResponse);

        List<RequestDtoResponse> requestsForOwner = itemRequestService.getRequestsForRequester(bookerId);

        assertNotNull(requestsForOwner);
        assertEquals(requestsForOwner.get(0).getId(), requestDtoResponse.getId());
        verify(itemRequestStorage, times(1)).findByRequestor_Id(bookerId);
    }

    @Test
    void getRequestByIdWhenRequestExistsThenReturnRequestDtoResponse() {
        when(userStorage.existsById(anyInt())).thenReturn(true);
        when(itemRequestStorage.findById(anyInt())).thenReturn(Optional.of(request));
        when(itemRequestMapper.toRequestDtoResponse(any(ItemRequest.class))).thenReturn(requestDtoResponse);

        RequestDtoResponse resultDto = itemRequestService.getRequestById(request.getId(), ownerId);

        assertNotNull(resultDto);
        assertEquals(requestDtoResponse.getId(), resultDto.getId());
        assertEquals(requestDtoResponse.getDescription(), resultDto.getDescription());

        verify(itemRequestStorage).findById(request.getId());
        verify(itemRequestMapper).toRequestDtoResponse(request);
    }

    @Test
    void getRequestByIdWhenUserDoesNotExistThenThrowException() {
        when(userStorage.existsById(anyInt())).thenReturn(false);

        assertThrows(UserNotExistsException.class, () -> itemRequestService.getRequestById(request.getId(), 999));
    }

    @Test
    void getRequestByIdWhenRequestDoesNotExistThenThrowException() {
        when(userStorage.existsById(anyInt())).thenReturn(true);
        when(itemRequestStorage.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RequestNotExistsException.class, () -> itemRequestService.getRequestById(999, ownerId));
    }

    @Test
    void getRequestsWhenUserExistsAndRequestsPresentThenReturnListOfRequestDtoResponse() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<ItemRequest> slice = new PageImpl<>(List.of(request));
        when(itemRequestStorage.findAllItemRequestsSortedByCreatedDesc(pageable, bookerId)).thenReturn(slice);
        when(itemRequestMapper.toRequestDtoResponse(any(ItemRequest.class))).thenReturn(requestDtoResponse);

        List<RequestDtoResponse> result = itemRequestService.getRequests(0, 10, bookerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(requestDtoResponse.getId(), result.get(0).getId());

        verify(itemRequestStorage).findAllItemRequestsSortedByCreatedDesc(pageable, bookerId);
        verify(itemRequestMapper, times(1)).toRequestDtoResponse(request);
    }

    @Test
    void getRequestsWhenNoRequestsPresentThenReturnEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<ItemRequest> slice = new PageImpl<>(Collections.emptyList());
        when(itemRequestStorage.findAllItemRequestsSortedByCreatedDesc(pageable, bookerId)).thenReturn(slice);

        List<RequestDtoResponse> result = itemRequestService.getRequests(0, 10, bookerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(itemRequestStorage).findAllItemRequestsSortedByCreatedDesc(pageable, bookerId);
        verify(itemRequestMapper, never()).toRequestDtoResponse(any(ItemRequest.class));
    }
}
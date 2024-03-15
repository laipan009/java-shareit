package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotExistsException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.RequestNotExistsException;
import ru.practicum.shareit.exception.UserNotExistsException;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {
    @Mock
    private ItemStorage itemStorage;

    @Mock
    private UserStorage userStorage;

    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemMapper itemMapper;

    @Mock
    private ItemRequestStorage requestStorage;

    @Mock
    private EntityManager entityManager;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    ItemService itemService;

    private BookingRequestDto bookingRequestDto;
    private User booker;
    private User owner;
    private Item item;
    private ItemDto itemDtoRequest;
    private ItemDto itemDtoResponse;
    private ItemDtoForOwner dtoForOwner;
    private Booking booking;
    private BookingResponseDto bookingResponseDto;
    private ShortBookingDto shortBookingDto;
    private ItemRequest request;
    private Comment comment;
    private CommentOutputDto commentOutputDto;
    private CommentInputDto commentInputDto;
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

        request = ItemRequest.builder()
                .id(requestId)
                .description("Есть у кого пылесос?")
                .created(LocalDateTime.now())
                .requestor(booker)
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

        commentInputDto = CommentInputDto.builder()
                .text(comment.getText())
                .authorId(bookerId)
                .itemId(itemId)
                .build();

        itemDtoResponse = ItemDto.builder()
                .id(itemId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(requestId)
                .build();

        dtoForOwner = ItemDtoForOwner.builder()
                .id(itemId)
                .lastBooking(null)
                .nextBooking(null)
                .requestId(requestId)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(List.of(commentOutputDto))
                .build();

        booking = Booking.builder()
                .id(1)
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();

        shortBookingDto = ShortBookingDto.builder()
                .id(booking.getId())
                .bookerId(booker.getId())
                .build();

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(booking.getId());
    }

    @Test
    void addItemWhenItemCreatedThenReturnItemDto() {
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.getItemFromDto(itemDtoRequest, owner)).thenReturn(item);
        when(requestStorage.findById(requestId)).thenReturn(Optional.ofNullable(request));
        when(itemStorage.save(item)).thenReturn(item);
        when(itemMapper.toItemDto(item)).thenReturn(itemDtoResponse);

        ItemDto result = itemService.addItem(itemDtoRequest, ownerId);

        assertNotNull(result);
        assertEquals(itemDtoResponse.getId(), result.getId());
        verify(itemStorage, times(1)).save(item);
    }

    @Test
    void addItemWhenUserNotExistThenReturnException() {
        when(userStorage.findById(ownerId)).thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class, () -> itemService.addItem(itemDtoRequest, ownerId));
    }

    @Test
    void addItemWhenRequestNotExistThenReturnException() {
        when(userStorage.findById(ownerId)).thenReturn(Optional.of(owner));
        when(itemMapper.getItemFromDto(itemDtoRequest, owner)).thenReturn(item);
        when(requestStorage.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RequestNotExistsException.class, () -> itemService.addItem(itemDtoRequest, ownerId));
    }

    @Test
    void updateItemWhenItemUpdatedThenReturnItemDto() {
        when(itemStorage.findById(itemId)).thenReturn(Optional.of(item));
        item.setName("Пылесос для химчистки");
        when(itemMapper.updateItemFromDto(item, itemDtoRequest)).thenReturn(item);
        when(itemStorage.save(any(Item.class))).thenReturn(item);
        itemDtoResponse.setName("Пылесос для химчистки");
        when(itemMapper.toItemDto(item)).thenReturn(itemDtoResponse);

        ItemDto result = itemService.updateItem(itemId, itemDtoRequest, ownerId);

        assertNotNull(result);
        assertEquals(itemId, result.getId());
        assertEquals("Пылесос для химчистки", result.getName());
        verify(itemStorage, times(1)).save(any(Item.class));
    }

    @Test
    void updateItemWhenItemNotExistThenThrowException() {
        when(itemStorage.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotExistsException.class, () -> itemService.updateItem(itemId, itemDtoRequest, ownerId));
    }

    @Test
    void updateItemWhenOwnerNotExistThenThrowException() {
        item.getOwner().setId(999);
        when(itemStorage.findById(itemId)).thenReturn(Optional.ofNullable(item));

        assertThrows(NotOwnerException.class, () -> itemService.updateItem(itemId, itemDtoRequest, ownerId));
    }

    @Test
    void getItemByIdWhenItemExistsAndUserIsOwnerThenReturnItemDtoForOwner() {
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        doNothing().when(entityManager).refresh(any());
        when(commentRepository.findCommentsByItem_Id(itemId)).thenReturn(List.of(comment));
        when(commentMapper.toOutputDtoFromComment(comment)).thenReturn(commentOutputDto);
        when(bookingRepository.findLastBookingByItemIdExcludingRejected(eq(itemId), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(bookingRepository.findNextBookingByItemIdExcludingRejected(eq(itemId), any(Pageable.class)))
                .thenReturn(Page.empty());
        when(itemMapper.toItemBookingDto(item, null, null, List.of(commentOutputDto)))
                .thenReturn(dtoForOwner);

        ItemDto result = itemService.getItemById(itemId, ownerId);

        assertNotNull(result);
        assertEquals(dtoForOwner.getId(), result.getId());
        assertEquals(dtoForOwner.getName(), result.getName());
        verify(itemStorage, times(1)).findItemById(itemId);
    }

    @Test
    void getItemByIdWhenItemDoesNotExistThenThrowItemNotExistsException() {
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotExistsException.class, () -> itemService.getItemById(itemId, ownerId));
    }


    @Test
    void getItemsByUserIdWhenUserExistsAndHasItemsThenReturnListOfItemDtoForOwner() {
        when(userStorage.existsById(ownerId)).thenReturn(true);
        when(itemStorage.findItemsByOwnerId(ownerId)).thenReturn(List.of(item));
        when(bookingRepository.findByItem_Owner_IdAndBookingStatusNotOrderByStartDesc(eq(ownerId), eq(BookingStatus.REJECTED)))
                .thenReturn(List.of(booking));
        when(commentRepository.findByItem_Owner_Id(ownerId)).thenReturn(List.of(comment));
        when(bookingMapper.toShortBookingDto(any(Booking.class))).thenReturn(shortBookingDto);
        when(itemMapper.toItemBookingDto(eq(item), isNull(), eq(shortBookingDto), anyList()))
                .thenReturn(dtoForOwner);

        List<ItemDtoForOwner> result = itemService.getItemsByUserId(ownerId);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(dtoForOwner.getId(), result.get(0).getId());
        verify(itemStorage, times(1)).findItemsByOwnerId(ownerId);
    }

    @Test
    void getItemsByUserIdWhenUserExistsAndHasNoItemsThenReturnEmptyList() {
        when(userStorage.existsById(ownerId)).thenReturn(true);
        when(itemStorage.findItemsByOwnerId(ownerId)).thenReturn(Collections.emptyList());

        List<ItemDtoForOwner> result = itemService.getItemsByUserId(ownerId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemStorage, times(1)).findItemsByOwnerId(ownerId);
    }

    @Test
    void getItemsByUserIdWhenUserDoesNotExistThenThrowUserNotExistsException() {
        when(userStorage.existsById(ownerId)).thenReturn(false);

        assertThrows(UserNotExistsException.class, () -> itemService.getItemsByUserId(ownerId));
    }

    @Test
    void searchItemsWhenItemsFoundThenReturnListOfItemDto() {
        String searchText = "пылесос";
        when(itemStorage.search(searchText)).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any(Item.class))).thenReturn(itemDtoResponse);

        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(itemDtoResponse.getId(), result.get(0).getId());
        verify(itemStorage, times(1)).search(searchText);
    }

    @Test
    void searchItemsWhenNoItemsFoundThenReturnEmptyList() {
        String searchText = "что-то очень редкое";
        when(itemStorage.search(searchText)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemStorage, times(1)).search(searchText);
    }

    @Test
    void searchItemsWhenSearchTextIsEmptyThenReturnEmptyList() {
        String searchText = "";
        when(itemStorage.search(searchText)).thenReturn(Collections.emptyList());

        List<ItemDto> result = itemService.searchItems(searchText);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(itemStorage, times(1)).search(searchText);
    }

    @Test
    void saveCommentWhenCommentIsValidThenReturnCommentOutputDto() {
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndUserIdAndEnded(itemId, bookerId)).thenReturn(true);
        when(commentMapper.toCommentFromInput(commentInputDto, booker, item)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.toOutputDtoFromComment(comment)).thenReturn(commentOutputDto);

        CommentOutputDto result = itemService.saveComment(commentInputDto, itemId, bookerId);

        assertNotNull(result);
        assertEquals("Хороший пылесос", result.getText());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void saveCommentWhenUserDoesNotExistThenThrowUserNotExistsException() {
        when(bookingRepository.existsByItemIdAndUserIdAndEnded(itemId, bookerId)).thenReturn(true);
        when(userStorage.findById(bookerId)).thenReturn(Optional.empty());

        assertThrows(UserNotExistsException.class, () -> itemService.saveComment(commentInputDto, itemId, bookerId));
    }

    @Test
    void saveCommentWhenItemDoesNotExistThenThrowItemNotExistsException() {
        when(bookingRepository.existsByItemIdAndUserIdAndEnded(itemId, bookerId)).thenReturn(true);
        when(userStorage.findById(bookerId)).thenReturn(Optional.of(booker));
        when(itemStorage.findItemById(itemId)).thenReturn(Optional.empty());

        assertThrows(ItemNotExistsException.class, () -> itemService.saveComment(commentInputDto, itemId, bookerId));
    }

    @Test
    void saveCommentWhenNoCompletedBookingThenThrowValidationException() {
        when(bookingRepository.existsByItemIdAndUserIdAndEnded(itemId, bookerId)).thenReturn(false);

        assertThrows(ValidationException.class, () -> itemService.saveComment(commentInputDto, itemId, bookerId));
    }
}
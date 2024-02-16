package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotExistsException;
import ru.practicum.shareit.exception.NotOwnerException;
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
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.storage.UserStorage;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @PersistenceContext
    private EntityManager entityManager;
    private final CommentMapper commentMapper;

    @Autowired
    public ItemService(ItemStorage itemStorage, UserStorage userStorage, BookingRepository bookingRepository, CommentRepository commentRepository, ItemMapper itemMapper, BookingMapper bookingMapper, EntityManager entityManager, CommentMapper commentMapper) {
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.itemMapper = itemMapper;
        this.bookingMapper = bookingMapper;
        this.entityManager = entityManager;
        this.commentMapper = commentMapper;
    }

    public ItemDto addItem(ItemDto itemDto, int userId) {
        log.info("Attempt to add new item by user with id {}", userId);
        User user = userStorage.findById(userId)
                .orElseThrow(() -> new UserNotExistsException("User with same id not exists"));
        Item mappedItem = itemMapper.getItemFromDto(itemDto, user);
        return itemMapper.toItemDto(itemStorage.save(mappedItem));
    }

    public ItemDto updateItem(int itemId, ItemDto itemDto, int userId) {
        log.info("Attempt to update item by id {} for user with id {}", itemId, userId);
        Item itemById = itemStorage.findById(itemId).orElseThrow();
        if (!itemById.getOwner().getId().equals(userId)) {
            throw new NotOwnerException("This user is not owner for this item");
        }
        Item updatedItem = itemMapper.updateItemFromDto(itemById, itemDto);
        itemStorage.save(updatedItem);
        return itemMapper.toItemDto(updatedItem);
    }

    @Transactional
    public ItemDto getItemById(int itemId, Integer userId) {
        log.info("Attempt to received item by id {}", itemId);
        Item itemById = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new ItemNotExistsException("Item not exists"));
        entityManager.refresh(itemById);
        List<CommentOutputDto> comments = commentRepository.findCommentsByItem_Id(itemId).stream()
                .map(commentMapper::toOutputDtoFromComment)
                .collect(Collectors.toList());

        if (Objects.equals(itemById.getOwner().getId(), userId)) {
            Pageable limitOne = PageRequest.of(0, 1);
            Booking last = bookingRepository.findLastBookingByItemIdExcludingRejected(itemId, limitOne)
                    .stream().findFirst().orElse(null);
            Booking next = bookingRepository.findNextBookingByItemIdExcludingRejected(itemId, limitOne)
                    .stream().findFirst().orElse(null);
            ShortBookingDto lastDto = null;
            ShortBookingDto nextDto = null;
            if (Optional.ofNullable(last).isPresent()) {
                lastDto = bookingMapper.toShortBookingDto(last);
            }
            if (Optional.ofNullable(next).isPresent()) {
                nextDto = bookingMapper.toShortBookingDto(next);
            }
            return itemMapper.toItemBookingDto(itemById, lastDto, nextDto, comments);
        }
        return itemMapper.toItemBookingDto(itemById, null, null, comments);
    }

    public List<ItemDtoForOwner> getItemsByUserId(int userId) {
        log.info("Attempt to received items by user id {}", userId);
        if (!userStorage.existsById(userId)) {
            throw new UserNotExistsException("User with id " + userId + " does not exist.");
        }

        List<Item> items = itemStorage.findItemsByOwnerId(userId);

        // Создаем Map для быстрого доступа к последнему и следующему бронированию для каждого предмета
        Map<Integer, ShortBookingDto> lastBookingsMap = new HashMap<>();
        Map<Integer, ShortBookingDto> nextBookingsMap = new HashMap<>();

        List<Booking> orderedBookings = bookingRepository.findByItem_Owner_IdAndBookingStatusNotOrderByStartDesc(userId, BookingStatus.REJECTED);

        LocalDateTime now = LocalDateTime.now();

        for (Item item : items) {
            Integer itemId = item.getId();

            List<Booking> itemBookings = orderedBookings.stream()
                    .filter(booking -> booking.getItem().getId().equals(itemId))
                    .collect(Collectors.toList());

            // Получаем последнее бронирование (самое позднее из прошедших)
            Booking lastBooking = itemBookings.stream()
                    .filter(booking -> booking.getEnd().isBefore(now))
                    .max(Comparator.comparing(Booking::getEnd))
                    .orElse(null);

            if (lastBooking != null) {
                lastBookingsMap.put(itemId, bookingMapper.toShortBookingDto(lastBooking));
            }

            // Получаем следующее бронирование (самое раннее из будущих)
            Booking nextBooking = itemBookings.stream()
                    .filter(booking -> booking.getStart().isAfter(now))
                    .min(Comparator.comparing(Booking::getStart))
                    .orElse(null);

            if (nextBooking != null) {
                nextBookingsMap.put(itemId, bookingMapper.toShortBookingDto(nextBooking));
            }
        }

        // Получаем список всех комментариев для предметов пользователя
        List<Comment> comments = commentRepository.findByItem_Owner_Id(userId);
        Map<Integer, List<CommentOutputDto>> commentsMap = comments.stream()
                .collect(Collectors.groupingBy(
                        comment -> comment.getItem().getId(),
                        Collectors.mapping(commentMapper::toOutputDtoFromComment, Collectors.toList())
                ));

        // Собираем все в список ItemDtoForOwner
        return items.stream().map(item -> {
            Integer itemId = item.getId();
            ShortBookingDto lastBookingDto = lastBookingsMap.get(itemId);
            ShortBookingDto nextBookingDto = nextBookingsMap.get(itemId);
            List<CommentOutputDto> itemComments = commentsMap.getOrDefault(itemId, Collections.emptyList());

            return itemMapper.toItemBookingDto(item, lastBookingDto, nextBookingDto, itemComments);
        }).collect(Collectors.toList());
    }

    public List<ItemDto> searchItems(String text) {
        log.info("Attempt to search items by key-word {}", text);
        return itemStorage.search(text).stream()
                .filter(Item::getAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentOutputDto saveComment(CommentInputDto commentInputDto, Integer itemId, Integer authorId) {
        log.info("Attempt to save comment by user id {}", authorId);
        if (!bookingRepository.existsByItemIdAndUserIdAndEnded(itemId, authorId)) {
            throw new ValidationException("This user does not have a completed booking for item by ID: " + itemId);
        }
        User author = userStorage.findById(authorId)
                .orElseThrow(() -> new UserNotExistsException("User with same id not exists"));
        Item itemById = itemStorage.findItemById(itemId)
                .orElseThrow(() -> new ItemNotExistsException("Item not exists"));
        Comment commentFromInput = commentMapper.toCommentFromInput(commentInputDto, author, itemById);
        Comment savedComment = commentRepository.save(commentFromInput);
        entityManager.refresh(savedComment);
        return commentMapper.toOutputDtoFromComment(savedComment);
    }
}
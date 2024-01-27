package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {
    List<Item> getAllItems();

    Item createItem(Item item);

    Item deleteItemById(int id);

    Item updateItem(Item item);

    Item getItemById(int id);

    List<Item> getItemsByUserId(int userId);
}

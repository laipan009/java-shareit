package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAllItems();

    Item createItem(Item item);

    Item deleteItemById(int id);

    Item updateItem(Item item);

    Item getItemById(int id);

    List<Item> getItemsByUserId(int userId);
}
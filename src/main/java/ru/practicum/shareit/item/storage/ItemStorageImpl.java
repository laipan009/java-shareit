package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemStorageImpl implements ItemStorage {
    private Integer idItem = 1;
    private Map<Integer, Item> items = new HashMap<>();

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item createItem(Item item) {
        item.setId(generateIdItem());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item deleteItemById(int id) {
        return items.remove(id);
    }

    @Override
    public Item updateItem(Item item) {
        return items.put(item.getId(), item);
    }

    @Override
    public Item getItemById(int id) {
        return items.get(id);
    }

    public List<Item> getItemsByUserId(int userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .collect(Collectors.toList());
    }

    private Integer generateIdItem() {
        return idItem++;
    }
}
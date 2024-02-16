package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemStorage extends JpaRepository<Item, Integer> {

    @EntityGraph(attributePaths = "owner")
    List<Item> findItemsByOwnerId(Integer ownerId);

    @Query(value = "SELECT * FROM items i " +
            "WHERE i.name ILIKE %:search% " +
            "OR i.description ILIKE %:search%", nativeQuery = true)
    List<Item> search(@Param("search") String text);

    @EntityGraph(attributePaths = "owner")
    Optional<Item> findItemById(Integer id);
}
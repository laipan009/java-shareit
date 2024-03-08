package ru.practicum.shareit.item.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemStorageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemStorage itemStorage;

    private User user;
    private Item item;
    private Item anotherItem;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        item = new Item();
        item.setName("Test Item 1");
        item.setDescription("Description for item 1");
        item.setAvailable(true);
        item.setOwner(user);
        entityManager.persist(item);

        anotherItem = new Item();
        anotherItem.setName("Another Test Item 2");
        anotherItem.setDescription("Description for item 2, also interesting");
        anotherItem.setAvailable(false);
        anotherItem.setOwner(user);
        entityManager.persist(anotherItem);
    }

    @Test
    void whenSearch_thenSuccess() {
        List<Item> items = itemStorage.search("Test");
        assertThat(items).hasSize(2).extracting("id")
                .containsExactlyInAnyOrder(item.getId(), anotherItem.getId());
    }

}
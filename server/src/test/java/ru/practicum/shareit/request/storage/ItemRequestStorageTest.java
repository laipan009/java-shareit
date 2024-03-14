package ru.practicum.shareit.request.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ItemRequestStorageTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestStorage itemRequestStorage;

    private User user, anotherUser;
    private ItemRequest request, someRequest;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("User 1");
        user.setEmail("user1@example.com");
        entityManager.persist(user);

        anotherUser = new User();
        anotherUser.setName("User 2");
        anotherUser.setEmail("user2@example.com");
        entityManager.persist(anotherUser);

        request = new ItemRequest();
        request.setDescription("Request 1");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now().minusDays(1));
        entityManager.persist(request);

        someRequest = new ItemRequest();
        someRequest.setDescription("Request 2");
        someRequest.setRequestor(anotherUser);
        someRequest.setCreated(LocalDateTime.now());
        entityManager.persist(someRequest);
    }

    @Test
    void whenFindByRequestor_Id_thenSuccess() {
        List<ItemRequest> requests = itemRequestStorage.findByRequestor_Id(user.getId());
        assertThat(requests).hasSize(1).extracting("id")
                .containsExactly(request.getId());
    }

    @Test
    void whenFindAllItemRequestsSortedByCreatedDesc_thenSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Slice<ItemRequest> requests = itemRequestStorage.findAllItemRequestsSortedByCreatedDesc(pageable, user.getId());
        assertThat(requests.getContent()).hasSize(1).extracting("id")
                .containsExactly(someRequest.getId());
    }
}
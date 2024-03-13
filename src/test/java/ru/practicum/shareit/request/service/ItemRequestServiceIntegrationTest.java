package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    @Autowired
    private EntityManager entityManager;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");
        entityManager.persist(user);

        entityManager.flush();
    }

    @Test
    void createItemRequestShouldCreateRequest() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.now());

        ItemRequestDto createdRequest = itemRequestService.createItemRequest(requestDto, user.getId());

        assertNotNull(createdRequest.getId());
        assertEquals(requestDto.getDescription(), createdRequest.getDescription());
    }

    @Test
    void getRequestsForRequesterShouldReturnRequests() {
        ItemRequestDto requestDto = new ItemRequestDto();
        requestDto.setDescription("Need a drill");
        requestDto.setCreated(LocalDateTime.now());
        itemRequestService.createItemRequest(requestDto, user.getId());

        List<RequestDtoResponse> requests = itemRequestService.getRequestsForRequester(user.getId());

        assertFalse(requests.isEmpty());
        assertEquals(1, requests.size());
        assertEquals(requestDto.getDescription(), requests.get(0).getDescription());
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemRequestDto itemRequestDto;
    private RequestDtoResponse requestDtoResponse;

    @BeforeEach
    void setUp() {
        itemRequestDto = ItemRequestDto.builder()
                .id(1)
                .description("Нужна строительная дрель, что бы соседи хорошо спали")
                .created(LocalDateTime.now())
                .build();

        requestDtoResponse = RequestDtoResponse.builder()
                .id(1)
                .description(itemRequestDto.getDescription())
                .created(itemRequestDto.getCreated())
                .build();
    }

    @Test
    void createItemRequestShouldReturnCreatedRequest() throws Exception {
        when(itemRequestService.createItemRequest(any(ItemRequestDto.class), anyInt())).thenReturn(itemRequestDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDto.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDto.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getRequestsForRequesterShouldReturnListRequestDtoResponse() throws Exception {
        when(itemRequestService.getRequestsForRequester(anyInt())).thenReturn(List.of(requestDtoResponse));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponse.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoResponse.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }

    @Test
    void getRequestByIdShouldReturnRequestDtoResponse() throws Exception {
        when(itemRequestService.getRequestById(anyInt(), anyInt())).thenReturn(requestDtoResponse);

        mockMvc.perform(get("/requests/{requestId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(requestDtoResponse.getId()))
                .andExpect(jsonPath("$.description").value(requestDtoResponse.getDescription()))
                .andExpect(jsonPath("$.created").exists());
    }

    @Test
    void getRequestsShouldReturnListRequestDtoResponse() throws Exception {
        when(itemRequestService.getRequests(anyInt(), anyInt(), anyInt())).thenReturn(List.of(requestDtoResponse));

        mockMvc.perform(get("/requests/all")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(2))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(requestDtoResponse.getId()))
                .andExpect(jsonPath("$[0].description").value(requestDtoResponse.getDescription()))
                .andExpect(jsonPath("$[0].created").exists());
    }
}
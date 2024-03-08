package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoForOwner;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        itemDto = new ItemDto();
        itemDto.setId(1);
        itemDto.setName("Drill");
        itemDto.setDescription("Powerful drill");
        itemDto.setAvailable(true);
    }

    @Test
    void addItemShouldReturnItem() throws Exception {
        when(itemService.addItem(any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void updateItemShouldReturnUpdatedItem() throws Exception {
        when(itemService.updateItem(anyInt(), any(ItemDto.class), anyInt())).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void getItemByIdShouldReturnItem() throws Exception {
        when(itemService.getItemById(anyInt(), anyInt())).thenReturn(itemDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()));
    }

    @Test
    void getItemsByUserIdShouldReturnItemsList() throws Exception {
        ItemDtoForOwner itemDtoForOwner = new ItemDtoForOwner();
        itemDtoForOwner.setId(1);
        itemDtoForOwner.setName("Drill");
        itemDtoForOwner.setDescription("Powerful drill");
        itemDtoForOwner.setAvailable(true);

        when(itemService.getItemsByUserId(anyInt())).thenReturn(Collections.singletonList(itemDtoForOwner));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));
    }

    @Test
    void searchItemsShouldReturnItemsList() throws Exception {
        when(itemService.searchItems(anyString())).thenReturn(Collections.singletonList(itemDto));

        mockMvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()));
    }

    @Test
    void saveCommentShouldReturnComment() throws Exception {
        CommentInputDto commentInputDto = new CommentInputDto();
        commentInputDto.setText("Great item!");
        commentInputDto.setItemId(1);
        commentInputDto.setAuthorId(1);

        CommentOutputDto commentOutputDto = new CommentOutputDto();
        commentOutputDto.setId(1);
        commentOutputDto.setText("Great item!");
        commentOutputDto.setAuthorName("John Doe");
        commentOutputDto.setCreated(LocalDateTime.now());

        when(itemService.saveComment(any(CommentInputDto.class), anyInt(), anyInt())).thenReturn(commentOutputDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentInputDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentOutputDto.getId()))
                .andExpect(jsonPath("$.text").value(commentOutputDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentOutputDto.getAuthorName()))
                .andExpect(jsonPath("$.created").exists());
    }

}
package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    @Autowired
    private ObjectMapper objectMapper;

    private BookingRequestDto bookingRequestDto;
    private BookingResponseDto bookingResponseDto;
    private Item item;
    private User user;

    @BeforeEach
    void setUp() {
        bookingRequestDto = new BookingRequestDto();
        bookingRequestDto.setItemId(1);
        bookingRequestDto.setStart(LocalDateTime.now().plusDays(1));
        bookingRequestDto.setEnd(LocalDateTime.now().plusDays(2));

        user = new User();
        user.setName("Test User");
        user.setEmail("test@example.com");

        item = Item.builder()
                .id(1)
                .name("Пылесос проф")
                .description("для химчистки диваном и ковров")
                .available(true)
                .owner(user)
                .build();

        bookingResponseDto = new BookingResponseDto();
        bookingResponseDto.setId(1);
        bookingResponseDto.setItem(new ItemDtoResponse());
        bookingResponseDto.setStart(LocalDateTime.now().plusDays(1));
        bookingResponseDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingResponseDto.setStatus(BookingStatus.WAITING);
    }

    @Test
    void createBookingRequestShouldReturnBooking() throws Exception {
        when(bookingService.createBookingRequest(any(), anyInt()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingRequestDto))
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void updateBookingShouldReturnUpdatedBooking() throws Exception {
        when(bookingService.updateBooking(anyInt(), anyInt(), Mockito.anyBoolean()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(patch("/bookings/{bookingId}", 1)
                        .param("approved", "true")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()))
                .andExpect(jsonPath("$.status").value(bookingResponseDto.getStatus().toString()));
    }

    @Test
    void getBookingByIdShouldReturnBooking() throws Exception {
        when(bookingService.getBookingById(anyInt(), anyInt()))
                .thenReturn(bookingResponseDto);

        mockMvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingResponseDto.getId()));
    }

    @Test
    void getBookingByUserShouldReturnBookingsList() throws Exception {
        when(bookingService.getBookingByUser(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponseDto));

        mockMvc.perform(get("/bookings")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()));
    }

    @Test
    void getBookingByOwnerShouldReturnBookingsList() throws Exception {
        when(bookingService.getBookingByOwner(anyString(), anyInt(), anyInt(), anyInt()))
                .thenReturn(Collections.singletonList(bookingResponseDto));

        mockMvc.perform(get("/bookings/owner")
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(bookingResponseDto.getId()));
    }
}
package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class RequestDtoResponse {
    private Integer id;

    @NotBlank(message = "Description cannot be null", groups = OnCreate.class)
    private String description;

    private LocalDateTime created = LocalDateTime.now();

    private List<ItemDto> items;
}
package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    private Integer id;

    @NotBlank(message = "Name cannot be null", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Description cannot be null", groups = OnCreate.class)
    private String description;

    @NotNull(message = "Availability status cannot be null", groups = OnCreate.class)
    private Boolean available;
    private Integer owner;
    private Integer request;
}
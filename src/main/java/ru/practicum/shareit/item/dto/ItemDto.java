package ru.practicum.shareit.item.dto;

import lombok.*;

import ru.practicum.shareit.validation.OnCreate;
import ru.practicum.shareit.validation.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
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

    public ItemDto(Integer id, String name, String description, Boolean available, Integer request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }

    public ItemDto(Integer id, String name, String description, Boolean available) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(String name, String description, Boolean available, Integer request) {
        this.name = name;
        this.description = description;
        this.available = available;
        this.request = request;
    }

    public ItemDto(String name, String description, Boolean available) {
        this.name = name;
        this.description = description;
        this.available = available;
    }

    public ItemDto(String name) {
        this.name = name;
    }

    public ItemDto(Boolean available) {
        this.available = available;
    }
}
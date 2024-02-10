package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Item {
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;
    private Boolean available;
    private Integer owner;
    private ItemRequest request;
}
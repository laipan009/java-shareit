package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemRequestDto {
    private Integer id;

    private String description;

    private LocalDateTime created = LocalDateTime.now();
}
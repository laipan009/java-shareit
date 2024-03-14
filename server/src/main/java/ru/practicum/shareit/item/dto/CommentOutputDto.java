package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentOutputDto {
    private Integer id;

    @NotBlank
    private String text;

    private String authorName;

    @NotBlank
    private LocalDateTime created;
}
package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;


@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
public class CommentInputDto {
    private Integer id;

    private String text;

    private Integer itemId;

    private Integer authorId;
}
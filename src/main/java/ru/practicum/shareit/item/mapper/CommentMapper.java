package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toCommentFromInput(CommentInputDto commentInputDto, User author, Item item) {
        return Comment.builder()
                .text(commentInputDto.getText())
                .author(author)
                .item(item)
                .build();
    }

    public static CommentOutputDto toOutputDtoFromComment(Comment comment) {
        return CommentOutputDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(LocalDateTime.now())
                .build();
    }
}
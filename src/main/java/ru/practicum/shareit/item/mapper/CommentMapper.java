package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.CommentOutputDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "author", source = "author"),
            @Mapping(target = "item", source = "item"),
            @Mapping(target = "text", source = "commentInputDto.text")
    })
    Comment toCommentFromInput(CommentInputDto commentInputDto, User author, Item item);

    @Mappings({
            @Mapping(target = "authorName", source = "author.name"),
            @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    })
    CommentOutputDto toOutputDtoFromComment(Comment comment);
}
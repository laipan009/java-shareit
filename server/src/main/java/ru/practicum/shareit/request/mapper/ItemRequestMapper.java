package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ItemRequestMapper {

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "description", source = "itemRequestDto.description"),
            @Mapping(target = "requestor", source = "user"),
            @Mapping(target = "created", source = "itemRequestDto.created")

    })
    ItemRequest toItemRequestFromDto(ItemRequestDto itemRequestDto, User user);

    @Mappings({
            @Mapping(target = "id", source = "itemRequest.id"),
            @Mapping(target = "description", source = "itemRequest.description"),
            @Mapping(target = "created", source = "itemRequest.created")
    })
    ItemRequestDto toDtoFromItemRequest(ItemRequest itemRequest);

    @Mapping(target = "items", ignore = true)
    RequestDtoResponse toRequestDtoResponse(ItemRequest itemRequest);
}
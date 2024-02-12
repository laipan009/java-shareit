package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.shareit.booking.dto.ShortBookingDto;
import ru.practicum.shareit.validation.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Data
@SuperBuilder
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemDtoForOwner extends ItemDto {
    private Integer id;

    @NotBlank(message = "Name cannot be null", groups = OnCreate.class)
    private String name;

    @NotBlank(message = "Description cannot be null", groups = OnCreate.class)
    private String description;

    @NotNull(message = "Availability status cannot be null", groups = OnCreate.class)
    private Boolean available;
    private ShortBookingDto lastBooking;
    private ShortBookingDto nextBooking;
    private List<CommentOutputDto> comments;
}
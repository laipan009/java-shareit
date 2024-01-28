package ru.practicum.shareit.user;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class User {
    private Integer id;

    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;
}
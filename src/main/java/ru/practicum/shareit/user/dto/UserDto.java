package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class UserDto {
    private Integer id;

    private String name;

    private String email;

    public UserDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserDto(String name) {
        this.name = name;
    }
}

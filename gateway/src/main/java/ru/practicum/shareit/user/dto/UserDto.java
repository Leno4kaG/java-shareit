package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {

    private long id;
    @NotNull
    private String name;
    @NotNull
    @Email(message = "Адрес электронной почты введен некорректно")
    private String email;
}

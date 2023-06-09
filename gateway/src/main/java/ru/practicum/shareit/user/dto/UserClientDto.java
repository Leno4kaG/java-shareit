package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;


@Data
public class UserClientDto {
    private long id;
    @NotNull
    @Email(message = "Адрес электронной почты введен некорректно")
    private String email;
}

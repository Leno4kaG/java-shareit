package ru.practicum.shareit.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorClientDto {

    private String error;
}

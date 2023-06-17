package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorHandlerTest {

    @Test
    void handleValidationException() {
        ErrorClientHandler errorHandler = new ErrorClientHandler();
        ErrorClientDto errorDto = errorHandler.handleValidationException(new ValidationClientException());
        assertEquals("Ошибка валидации", errorDto.getError());
    }
}
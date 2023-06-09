package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorHandlerTest {

    @Test
    void handleUserNotFound() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorDto errorDto = errorHandler.handleUserNotFound(new ValidationException());
        assertEquals("Ошибка валидации", errorDto.getError());
    }
}
package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorDtoTest {

    @Test
    void getError() {
        ErrorDto errorDto = new ErrorDto("test");

        assertEquals("test", errorDto.getError());
    }

    @Test
    void setError() {
        ErrorDto errorDto = new ErrorDto("test");
        errorDto.setError("error");
        assertEquals("error", errorDto.getError());
    }
}
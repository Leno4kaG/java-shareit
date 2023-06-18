package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @Test
    void handleUserNotFound() {
        ErrorHandler errorHandler = new ErrorHandler();
        ErrorDto errorDto = errorHandler.handleUserNotFound(new ValidationException());
        assertEquals("Ошибка валидации", errorDto.getError());
    }

    @Test
    void handleMethodArgumentNotValidException() {
        ErrorHandler errorHandler = new ErrorHandler();
        MethodArgumentNotValidException err = mock(MethodArgumentNotValidException.class);
        when(err.getMessage()).thenReturn("Ошибка валидации");
        ErrorDto errorDto = errorHandler.handleMethodArgumentNotValidException(err);
        assertEquals("Ошибка валидации", errorDto.getError());

    }

    @Test
    void handleMethodArgumentTypeMismatchException() {
        ErrorHandler errorHandler = new ErrorHandler();
        Class type = String.class;

        WebRequest webRequest = mock(WebRequest.class);
        MethodArgumentTypeMismatchException err = mock(MethodArgumentTypeMismatchException.class);
        when(err.getRequiredType()).thenReturn(type);

        ResponseEntity<Object> errorDto = errorHandler.handleMethodArgumentTypeMismatchException(err,
                webRequest);

        assertEquals(HttpStatus.BAD_REQUEST, errorDto.getStatusCode());
    }
}
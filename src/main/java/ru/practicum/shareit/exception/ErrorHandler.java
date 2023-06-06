package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleUserNotFound(UserNotFoundException e) {
        log.error("User with id {} not found", e.getUserId());
        return new ErrorDto("Пользователь не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleUserNotFound(ItemNotFoundException e) {
        log.error("Item with id {} not found", e.getItemId());
        return new ErrorDto("Товар не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleUserNotFound(BookingNotFoundException e) {
        log.error("Booking with id {} not found", e.getBookingId());
        return new ErrorDto("Бронирование не найдено");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleUserNotFound(ItemRequestNotFoundException e) {
        log.error("Item request with id {} not found", e.getItemRequestId());
        return new ErrorDto("Запрос на добавление товара/вещи не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleUserNotFound(ValidationException e) {
        log.error(e.getMessage());
        return new ErrorDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ErrorDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(PageParamException e) {
        log.error(e.getMessage());
        return new ErrorDto("Ошибка валидации параметров страницы");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(BookingValidationException e) {
        log.error(e.getMessage());
        return new ErrorDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(CommentValidationException e) {
        log.error(e.getMessage());
        return new ErrorDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleUserNotFound(BookingException e) {
        log.error(e.getMessage());
        return new ErrorDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleUserNotFound(Exception e) {
        log.error(e.getMessage());
        return new ErrorDto("Произошла непредвиденная ошибка.");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<Object> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        Class<?> type = e.getRequiredType();
        String message;
        if (type.isEnum()) {
            message = String.format("Unknown state: %s", request.getParameter("state"));
        } else {
            message = "The parameter " + e.getName() + " must be of type " + type.getTypeName();
        }
        return ResponseEntity.badRequest().body(new ErrorDto(message));
    }


}

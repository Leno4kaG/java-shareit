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
public class ErrorClientHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorClientDto handleUserNotFound(UserClientNotFoundException e) {
        log.error("User with id {} not found", e.getUserId());
        return new ErrorClientDto("Пользователь не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorClientDto handleItemNotFound(ItemClientNotFoundException e) {
        log.error("Item with id {} not found", e.getItemId());
        return new ErrorClientDto("Товар не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorClientDto handleBookingNotFound(BookingClientNotFoundException e) {
        log.error("Booking with id {} not found", e.getBookingId());
        return new ErrorClientDto("Бронирование не найдено");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorClientDto handleItemReqNotFound(ItemRequestClientNotFoundException e) {
        log.error("Item request with id {} not found", e.getItemRequestId());
        return new ErrorClientDto("Запрос на добавление товара/вещи не найден");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorClientDto handleValidationException(ValidationClientException e) {
        log.error(e.getMessage());
        return new ErrorClientDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorClientDto handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        return new ErrorClientDto("Ошибка валидации");
    }


    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorClientDto handleBookingValidException(BookingClientValidationException e) {
        log.error(e.getMessage());
        return new ErrorClientDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorClientDto handleCommentValidException(CommentClientValidationException e) {
        log.error(e.getMessage());
        return new ErrorClientDto("Ошибка валидации");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorClientDto handleBookingException(BookingClientException e) {
        log.error(e.getMessage());
        return new ErrorClientDto(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorClientDto handleOtherException(Exception e) {
        log.error(e.getMessage());
        return new ErrorClientDto("Произошла непредвиденная ошибка.");
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
        return ResponseEntity.badRequest().body(new ErrorClientDto(message));
    }


}

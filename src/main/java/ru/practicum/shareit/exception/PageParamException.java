package ru.practicum.shareit.exception;

public class PageParamException extends RuntimeException {

    public PageParamException() {
    }

    public PageParamException(String message) {
        super(message);
    }
}

package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    private long userId;

    public UserNotFoundException(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }
}

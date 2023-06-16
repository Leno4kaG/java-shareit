package ru.practicum.shareit.exception;

public class UserClientNotFoundException extends RuntimeException {
    private long userId;

    public UserClientNotFoundException(long userId) {
        this.userId = userId;
    }

    public long getUserId() {
        return this.userId;
    }
}

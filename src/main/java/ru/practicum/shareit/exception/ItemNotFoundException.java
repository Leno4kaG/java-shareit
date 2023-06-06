package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {

    private long itemId;

    public ItemNotFoundException(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return this.itemId;
    }
}

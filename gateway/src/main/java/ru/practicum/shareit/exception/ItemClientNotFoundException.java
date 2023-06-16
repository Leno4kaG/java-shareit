package ru.practicum.shareit.exception;

public class ItemClientNotFoundException extends RuntimeException {

    private long itemId;

    public ItemClientNotFoundException(long itemId) {
        this.itemId = itemId;
    }

    public long getItemId() {
        return this.itemId;
    }
}

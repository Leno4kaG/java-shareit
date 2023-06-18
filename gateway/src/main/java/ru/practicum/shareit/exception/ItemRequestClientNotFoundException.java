package ru.practicum.shareit.exception;

public class ItemRequestClientNotFoundException extends RuntimeException {

    private final Long itemRequestId;

    public ItemRequestClientNotFoundException(Long itemRequestId) {
        this.itemRequestId = itemRequestId;
    }


    public Long getItemRequestId() {
        return this.itemRequestId;
    }
}

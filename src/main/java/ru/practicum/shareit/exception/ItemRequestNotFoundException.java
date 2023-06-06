package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {

    private final Long itemRequestId;

    public ItemRequestNotFoundException(Long itemRequestId) {
        this.itemRequestId = itemRequestId;
    }

    public ItemRequestNotFoundException(String message, Long itemRequestId) {
        super(message);
        this.itemRequestId = itemRequestId;
    }

    public Long getItemRequestId() {
        return this.itemRequestId;
    }
}

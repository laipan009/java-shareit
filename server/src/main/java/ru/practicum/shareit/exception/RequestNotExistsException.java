package ru.practicum.shareit.exception;

public class RequestNotExistsException extends RuntimeException {
    public RequestNotExistsException(String message) {
        super(message);
    }
}

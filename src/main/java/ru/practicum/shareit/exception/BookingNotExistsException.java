package ru.practicum.shareit.exception;

public class BookingNotExistsException extends RuntimeException {
    public BookingNotExistsException(String message) {
        super(message);
    }
}
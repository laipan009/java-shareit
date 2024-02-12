package ru.practicum.shareit.exception;

import javax.validation.ValidationException;

public class ItemNotAvailableException extends ValidationException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
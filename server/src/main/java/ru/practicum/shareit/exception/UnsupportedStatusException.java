package ru.practicum.shareit.exception;

import javax.validation.ValidationException;

public class UnsupportedStatusException extends ValidationException {
    public UnsupportedStatusException(String message) {
        super(message);
    }
}
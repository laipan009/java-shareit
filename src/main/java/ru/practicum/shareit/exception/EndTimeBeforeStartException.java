package ru.practicum.shareit.exception;

import javax.validation.ValidationException;

public class EndTimeBeforeStartException extends ValidationException {
    public EndTimeBeforeStartException(String message) {
        super(message);
    }
}
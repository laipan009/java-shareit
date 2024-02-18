package ru.practicum.shareit.exception;

public class IllegalAccessForUserException extends RuntimeException {
    public IllegalAccessForUserException(String message) {
        super(message);
    }
}
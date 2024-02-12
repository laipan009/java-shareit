package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({UserNotExistsException.class,
            ItemNotExistsException.class,
            BookingNotExistsException.class,
            IllegalAccessForUserException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotExistsEntity(final RuntimeException e) {
        log.error("Invoke exception: " + e.getMessage());
        return Map.of("Error: User not exists", "try again");
    }

    @ExceptionHandler(NotOwnerException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleNotOwner(final NotOwnerException e) {
        log.error("Invoke exception: " + e.getMessage());
        return Map.of("Error: This user is not owner for this item", "try again");
    }

    @ExceptionHandler({ValidationException.class,
            EndTimeBeforeStartException.class,
            ItemNotAvailableException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotOwner(final ValidationException e) {
        log.error("Invoke exception: " + e.getMessage());
        return Map.of("Error: Validation exception", "try again");
    }

    @ExceptionHandler(UnsupportedStatusException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public Map<String, String> validationException(UnsupportedStatusException exception) {
        log.error(exception.getMessage());
        return Map.of("error", exception.getMessage());
    }
}
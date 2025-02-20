package ru.yandex.practicum.filmorate.exception;

import org.slf4j.Logger;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Logger logger) {
        this(message);
        logger.warn(message);
    }
}

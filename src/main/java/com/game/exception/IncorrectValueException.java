package com.game.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class IncorrectValueException extends RuntimeException {

    public IncorrectValueException() {
    }

    public IncorrectValueException(String message) {
        super(message);
    }
}

package ru.kirill.controller.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class VolunteerServiceException extends RuntimeException {
    private final HttpStatus status;

    public VolunteerServiceException(String msg, HttpStatus httpStatus) {
        super(msg);
        this.status = httpStatus;
    }
}

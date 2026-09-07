package ru.kirill.controller.exception;

import org.springframework.http.HttpStatus;

public class VolunteerServiceException extends Throwable {
    public VolunteerServiceException(String msg, HttpStatus httpStatus) {

    }
}

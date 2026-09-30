package ru.kirill.controller.exception;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class VolunteerExceptionHandler {

    @ExceptionHandler(VolunteerServiceException.class)
    public ProblemDetail handle(VolunteerServiceException e) {
        return ProblemDetail.forStatusAndDetail(e.getStatus(), e.getMessage());
    }
}

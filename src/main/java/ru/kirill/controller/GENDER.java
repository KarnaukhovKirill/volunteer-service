package ru.kirill.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.http.HttpStatus;

public enum GENDER {
    MALE,FEMALE;

//    @JsonCreator
//    public static GENDER fromString(String str) {
//        try {
//            return GENDER.valueOf(str.toUpperCase());
//        } catch (IllegalArgumentException e) {
//            var msg = "Failed to create Category from string: %s".formatted(str);
//            throw new VolunteerServiceException(msg, HttpStatus.BAD_REQUEST);
//        }
//    }
}

package ru.kirill.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolunteerContact {
    @NotBlank(message = "ФИО не должно быть пустым")
    private final String fio;
    @NotNull(message = "Пол не должен быть null")
    private final GENDER gender;
    @NotBlank(message = "Номер телефона не должен быть пустым")
    private final String phoneNumber;
    @NotBlank(message = "email не должен быть пустым")
    private final String email;
}

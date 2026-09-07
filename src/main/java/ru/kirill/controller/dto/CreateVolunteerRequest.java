package ru.kirill.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class CreateVolunteerRequest {
    @NotBlank(message = "ФИО не должно быть пустым")
    private final String fio;
    @NotNull(message = "Пол не должен быть null")
    private final GENDER gender;
    @NotBlank(message = "Номер телефона не должен быть пустым")
    private final String phoneNumber;
    @NotBlank(message = "email не должен быть пустым")
    private final String email;
    @NotNull(message = "Дата рождения не должна быть пустой")
    @JsonFormat(pattern="yyyy-MM-dd")
    private final LocalDate birthday;
    @NotBlank(message = "Город проживания не должен быть пустым")
    private final String city;
    private final String restrict;
}

package ru.kirill.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class UpdateVolunteerRequest {
    @NotNullOrNotBlank(message = "Фамилия не должна быть пустой")
    private final String lastName;
    @NotNullOrNotBlank(message = "Номер телефона не должен быть пустым")
    private final String phoneNumber;
    @NotNullOrNotBlank(message = "email не должен быть пустым")
    private final String email;
    @NotNullOrNotBlank(message = "Город проживания не должен быть пустым")
    private final String city;
    @NotNullOrNotBlank(message = "Район не должен быть пустым")
    private final String restrict;
}

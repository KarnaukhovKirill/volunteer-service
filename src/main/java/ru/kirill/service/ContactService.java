package ru.kirill.service;

import jakarta.validation.constraints.NotBlank;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.entity.Volunteer;

import java.util.UUID;

public interface ContactService {
    void create(Volunteer savedVolunteer, @NotBlank(message = "email не должен быть пустым") String email, @NotBlank(message = "Номер телефона не должен быть пустым") String phoneNumber);

    void update(UUID volunteereUUID, UpdateVolunteerRequest request);
}

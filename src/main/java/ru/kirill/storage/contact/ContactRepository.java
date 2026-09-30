package ru.kirill.storage.contact;

import ru.kirill.controller.dto.UpdateVolunteerRequest;

import java.util.UUID;

public interface ContactRepository {
    void update(UUID userId, UpdateVolunteerRequest request);
}

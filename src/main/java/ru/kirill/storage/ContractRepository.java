package ru.kirill.storage;

import ru.kirill.controller.dto.UpdateVolunteerRequest;

import java.util.UUID;

public interface ContractRepository {
    void update(UUID userId, UpdateVolunteerRequest request);
}

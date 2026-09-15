package ru.kirill.storage;

import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VolunteerRepository {
    List<Volunteer> get(VolunteerListRequest request);
    UUID update(String userId, UpdateVolunteerRequest request);
}

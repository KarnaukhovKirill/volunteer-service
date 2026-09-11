package ru.kirill.storage;

import io.micrometer.observation.ObservationFilter;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VolunteerRepository {
    Optional<VolunteerInfo> findByName(String userId);

    List<VolunteerInfo> get(VolunteerListRequest request);

    VolunteerInfo create(VolunteerInfo volunteerInfo);

    void delete(String userId);

    UUID update(String userId, UpdateVolunteerRequest request);

    Optional<VolunteerInfo> findById(UUID id);
}

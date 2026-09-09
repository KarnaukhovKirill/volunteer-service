package ru.kirill.storage;

import ru.kirill.controller.dto.VolunteerInfoDto;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.util.Optional;

public interface VolunteerRepository {
    Optional<VolunteerInfo> findByName(String username);

    VolunteerInfoDto get(VolunteerListRequest request);
}

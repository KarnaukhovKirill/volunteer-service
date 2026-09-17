package ru.kirill.storage.volunteer;

import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;

import java.util.List;
import java.util.UUID;

public interface VolunteerRepository {
    List<Volunteer> get(VolunteerListRequest request);
    int update(String userId, UpdateVolunteerRequest request);
}

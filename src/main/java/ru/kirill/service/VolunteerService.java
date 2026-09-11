package ru.kirill.service;

import ru.kirill.controller.dto.*;

import java.util.UUID;

public interface VolunteerService {

    VolunteerInfoDto create(CreateVolunteerRequest request, String userId);

    VolunteerInfoDto get(String username);

    void delete(String username);

    VolunteerInfoDto update(String userId, UpdateVolunteerRequest request);

    void proveAction(ProveVolunteer prove, String userId);

    VolunteerInfosResponse get(VolunteerListRequest request);

    VolunteerInfoDto get(UUID id);
}

package ru.kirill.storage;

import org.springframework.stereotype.Repository;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerInfoDto;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VolunteerRepositoryImpl implements VolunteerRepository {
    @Override
    public Optional<VolunteerInfo> findByName(String userId) {
        return Optional.empty();
    }

    @Override
    public List<VolunteerInfo> get(VolunteerListRequest request) {
        return List.of();
    }

    @Override
    public VolunteerInfo create(VolunteerInfo volunteerInfo) {
        return null;
    }

    @Override
    public void delete(String userId) {

    }

    @Override
    public UUID update(String userId, UpdateVolunteerRequest request) {
        return UUID.randomUUID();
    }

    @Override
    public Optional<VolunteerInfo> findById(UUID id) {
        return Optional.empty();
    }
}

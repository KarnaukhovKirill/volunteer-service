package ru.kirill.storage;

import org.springframework.stereotype.Repository;
import ru.kirill.controller.dto.VolunteerInfoDto;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.util.Optional;

@Repository
public class VolunteerRepositoryImpl implements VolunteerRepository {
    @Override
    public Optional<VolunteerInfo> findByName(String username) {
        return Optional.empty();
    }

    @Override
    public VolunteerInfo get(VolunteerListRequest request) {
        return null;
    }

    @Override
    public VolunteerInfo create(VolunteerInfo volunteerInfo) {
        return null;
    }
}

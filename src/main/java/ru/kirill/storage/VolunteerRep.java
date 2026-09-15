package ru.kirill.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VolunteerRep extends JpaRepository<Volunteer, UUID>, VolunteerRepository {
    List<Volunteer> findByUserId(String userId);

    void deleteByUserId(String userId);
}

package ru.kirill.storage.volunteer;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kirill.storage.entity.Volunteer;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VolunteerRep extends JpaRepository<Volunteer, UUID>, VolunteerRepository {
    List<Volunteer> findByUserId(String userId);

    void deleteByUserId(String userId);
}

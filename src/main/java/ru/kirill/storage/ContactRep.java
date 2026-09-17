package ru.kirill.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kirill.storage.entity.ContactInfo;
import ru.kirill.storage.entity.Volunteer;

import java.util.List;
import java.util.UUID;

public interface ContactRep extends JpaRepository<ContactInfo, UUID> {
    List<ContactInfo> getByVolunteerId(Volunteer volunteerId);
}

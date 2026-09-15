package ru.kirill.storage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LocationRep extends JpaRepository<Location, UUID> {
    List<Location> findByName(String name);
}

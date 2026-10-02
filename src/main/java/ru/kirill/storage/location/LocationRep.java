package ru.kirill.storage.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kirill.storage.entity.Location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface LocationRep extends JpaRepository<Location, UUID> {
    List<Location> findByName(String name);

    /**
     * Ищет локацию по названию, при отсутствии создаёт.
     * saveAndFlush: bulk update не флашит таблицу location, без этого получим нарушение FK
     */
    default Location findOrCreate(String name) {
        return findByName(name)
                .stream()
                .findFirst()
                .orElseGet(() -> saveAndFlush(
                        Location.builder().name(name).locationKind(Location.LOCATIONKIND.PARENT).createDate(LocalDateTime.now()).build()
                ));
    }
}

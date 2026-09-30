package ru.kirill.storage.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.BaseTests;
import ru.kirill.controller.dto.STATUS;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.searchers.VolunteerSearcherers;
import ru.kirill.storage.updaters.VolunteerUpdaters;
import ru.kirill.storage.volunteer.VolunteerRepositoryImpl;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@Import({VolunteerRepositoryImpl.class, VolunteerUpdaters.class, VolunteerSearcherers.class})
@Transactional(propagation = Propagation.NEVER)
public class VolunteerRepositoryTest extends BaseTests {
    @Autowired
    private EntityManager em;

    @Test
    public void updateLastName() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .lastName("Ivanov")
                .build();

        int updated = volunteerRepository.update("User001", request);

        assertEquals(1, updated);
        Volunteer volunteer = em.createQuery("select v from Volunteer v where v.userId = ?1", Volunteer.class)
                .setParameter(1, "User001")
                .getSingleResult();
        assertEquals("Ivanov", volunteer.getLastName());
    }

    @Test
    public void updateLocation() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .city("Moscow")
                .restrict("Metro1")
                .build();

        int updated = volunteerRepository.update("User001", request);

        assertEquals(1, updated);

        Volunteer volunteer = em.createQuery("select v from Volunteer v join fetch v.location where v.userId = ?1", Volunteer.class)
                .setParameter(1, "User001")
                .getSingleResult();

        assertEquals("Moscow(Metro1)", volunteer.getLocation().getName());
    }

    @Test
    public void getByCityOnly() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .city("moscow")
                .build();

        assertEquals(Set.of("User002", "User003"), getUserIds(request));
    }

    @Test
    public void getByCityAndRestrict() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .city("Moscow")
                .restrict("Khimky")
                .build();

        assertEquals(Set.of("User003"), getUserIds(request));
    }

    @Test
    public void getByCityAndRestrictIsCaseSensitive() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .city("moscow")
                .restrict("khimky")
                .build();

        assertEquals(Set.of(), getUserIds(request));
    }

    @Test
    public void getByNotExistingCity() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .city("Tver")
                .build();

        assertEquals(Set.of(), getUserIds(request));
    }

    @Test
    public void getByRestrictWithoutCityIgnoresLocation() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .restrict("Khimky")
                .build();

        assertEquals(Set.of("User001", "User002", "User003", "User004", "User005", "Username"), getUserIds(request));
    }

    @Test
    public void getByCityAndStatus() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .city("Moscow")
                .status(STATUS.AVAILABLE)
                .build();

        assertEquals(Set.of("User002"), getUserIds(request));
    }

}

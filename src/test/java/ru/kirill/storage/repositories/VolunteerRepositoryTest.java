package ru.kirill.storage.repositories;

import org.junit.jupiter.api.Test;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest
@Import({VolunteerRepositoryImpl.class, VolunteerUpdaters.class, VolunteerSearcherers.class})
@Transactional(propagation = Propagation.NEVER)
public class VolunteerRepositoryTest extends BaseTests {

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
        assertEquals("Moscow(Metro1)", getLocationName("User001"));
    }

    @Test
    public void updateCityWithoutRestrict_locationIsCity() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .city("Kazan")
                .build();

        int updated = volunteerRepository.update("User002", request);

        assertEquals(1, updated);
        assertEquals("Kazan", getLocationName("User002"));
    }

    @Test
    public void updateRestrictWithoutCity_locationNotChanged() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .restrict("Khimky")
                .build();

        int updated = volunteerRepository.update("User002", request);

        assertEquals(1, updated);
        assertEquals("Moscow", getLocationName("User002"));
    }

    @Test
    public void updateExistingLocation_reusesLocation() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .city("Moscow")
                .restrict("Khimky")
                .build();

        volunteerRepository.update("User001", request);

        assertEquals(UUID.fromString("00000000-0000-0000-0000-000000000002"), getVolunteer("User001").getLocation().getId());
        Long count = em.createQuery("select count(l) from Location l where l.name = ?1", Long.class)
                .setParameter(1, "Moscow(Khimky)")
                .getSingleResult();
        assertEquals(1L, count);
    }

    @Test
    public void updateNotExistingVolunteer_returnsZero() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .lastName("Ivanov")
                .build();

        assertEquals(0, volunteerRepository.update("NotExisting", request));
    }

    @Test
    public void updateAffectsOnlyRequestedVolunteer() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .lastName("Ivanov")
                .build();

        volunteerRepository.update("User001", request);

        Volunteer other = getVolunteer("User002");
        assertEquals("Petrov", other.getLastName());
        assertNull(other.getUpdateDate());
    }

    @Test
    public void updateLastName_keepsLocation() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .lastName("Ivanov")
                .build();

        volunteerRepository.update("User002", request);

        assertEquals("Ivanov", getVolunteer("User002").getLastName());
        assertEquals("Moscow", getLocationName("User002"));
    }

    @Test
    public void updateLocation_keepsLastName() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .city("Kazan")
                .restrict("Center")
                .build();

        volunteerRepository.update("User002", request);

        assertEquals("Petrov", getVolunteer("User002").getLastName());
        assertEquals("Kazan(Center)", getLocationName("User002"));
    }

    @Test
    public void updateEmptyRequest_onlyUpdateDateChanged() {
        int updated = volunteerRepository.update("User002", UpdateVolunteerRequest.builder().build());

        assertEquals(1, updated);
        Volunteer volunteer = getVolunteer("User002");
        assertNotNull(volunteer.getUpdateDate());
        assertEquals("Petrov", volunteer.getLastName());
        assertEquals("Moscow", getLocationName("User002"));
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

    @Test
    public void getByStatusOnly() {
        VolunteerListRequest request = VolunteerListRequest.builder()
                .status(STATUS.AVAILABLE)
                .build();

        assertEquals(Set.of("User001", "User002", "User004", "Username"), getUserIds(request));
    }

    @Test
    public void getWithoutFilters_returnsAll() {
        VolunteerListRequest request = VolunteerListRequest.builder().build();

        assertEquals(Set.of("User001", "User002", "User003", "User004", "User005", "Username"), getUserIds(request));
    }

    @Test
    public void getByCityWithLikeWildcards_wildcardsAreEscaped() {
        assertEquals(Set.of(), getUserIds(VolunteerListRequest.builder().city("Mos%").build()));
        assertEquals(Set.of(), getUserIds(VolunteerListRequest.builder().city("Mosco_").build()));
    }

    private Volunteer getVolunteer(String userId) {
        return em.createQuery("select v from Volunteer v left join fetch v.location where v.userId = ?1", Volunteer.class)
                .setParameter(1, userId)
                .getSingleResult();
    }
}

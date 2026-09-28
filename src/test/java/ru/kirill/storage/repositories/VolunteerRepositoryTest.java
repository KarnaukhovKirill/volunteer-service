package ru.kirill.storage.repositories;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.updaters.VolunteerUpdaters;
import ru.kirill.storage.volunteer.VolunteerRep;
import ru.kirill.storage.volunteer.VolunteerRepository;
import ru.kirill.storage.volunteer.VolunteerRepositoryImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import({VolunteerRepositoryImpl.class, VolunteerUpdaters.class})
@SqlGroup({
        @Sql(scripts = "classpath:insert-volunteer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:clear-volunteers.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Transactional(propagation = Propagation.NEVER)
public class VolunteerRepositoryTest {
    @Autowired
    private EntityManager em;
    @Autowired
    private VolunteerRep volunteerRepository;

    @Test
    public void updateLastName() {
        UpdateVolunteerRequest request = UpdateVolunteerRequest.builder()
                .lastName("Ivanov")
                .build();

        int updated = volunteerRepository.update("User001", request);
        em.clear();

        assertEquals(1, updated);
        Volunteer volunteer = em.createQuery("select v from Volunteer v where v.userId = ?1", Volunteer.class)
                .setParameter(1, "User001")
                .getSingleResult();
        assertEquals("User", volunteer.getLastName());
    }
}

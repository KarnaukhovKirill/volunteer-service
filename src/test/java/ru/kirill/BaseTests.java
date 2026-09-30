package ru.kirill;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.volunteer.VolunteerRep;

import java.util.Set;
import java.util.stream.Collectors;

@SqlGroup({
        @Sql(scripts = "classpath:insert-volunteer.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD),
        @Sql(scripts = "classpath:clear-volunteers.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public abstract class BaseTests {
    @Autowired
    protected VolunteerRep volunteerRepository;

    protected Set<String> getUserIds(VolunteerListRequest request) {
        return volunteerRepository.get(request)
                .stream()
                .map(Volunteer::getUserId)
                .collect(Collectors.toSet());
    }
}

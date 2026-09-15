package ru.kirill.storage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class VolunteerRepositoryImpl implements VolunteerRepository {
    @Autowired
    EntityManager entityManager;
    @Autowired
    LocationRep locationRep;

    @Override
    public List<Volunteer> get(VolunteerListRequest request) {
        return List.of();
    }

    @Override
    public UUID update(String userId, UpdateVolunteerRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Volunteer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Volunteer.class);
        Root<Volunteer> root = criteriaUpdate.from(Volunteer.class);

        if (request.getCity() != null) {
            String name = request.getCity() + "(" + request.getRestrict() + ")";
            List<Location> locations = locationRep.findByName(name);
            Location savedLocation = locations.stream().findFirst().orElse(locationRep.save(Location.builder().name(name).locationKind(LOCATIONKIND.PARENT).createDate(LocalDateTime.now()).build()));
            criteriaUpdate.set(Volunteer_.LOCATION, savedLocation);
        }

        if (request.getLastName() != null) criteriaUpdate.set(Volunteer_.LAST_NAME, request.getLastName());
        criteriaUpdate.set(Volunteer_.UPDATE_DATE, LocalDateTime.now());

        criteriaUpdate.where(criteriaBuilder.equal(root.get(Volunteer_.userId), userId));
        return UUID.randomUUID();
    }
}

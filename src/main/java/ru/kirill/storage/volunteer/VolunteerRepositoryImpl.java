package ru.kirill.storage.volunteer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.entity.Volunteer_;
import ru.kirill.storage.updaters.VolunteerUpdater;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class VolunteerRepositoryImpl implements VolunteerRepository {
    EntityManager entityManager;
    private final List<VolunteerUpdater<?>> updaters;

    public VolunteerRepositoryImpl(EntityManager entityManager, List<VolunteerUpdater<?>> updaters) {
        this.entityManager = entityManager;
        this.updaters = updaters;
    }

    @Override
    public List<Volunteer> get(VolunteerListRequest request) {
        return List.of();
    }

    @Override
    public int update(String userId, UpdateVolunteerRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Volunteer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Volunteer.class);
        Root<Volunteer> root = criteriaUpdate.from(Volunteer.class);

        updaters.forEach(updater -> updater.setAttr(criteriaUpdate, request));
        criteriaUpdate.set(Volunteer_.UPDATE_DATE, LocalDateTime.now());

        criteriaUpdate.where(criteriaBuilder.equal(root.get(Volunteer_.userId), userId));

        return entityManager.createQuery(criteriaUpdate).executeUpdate();
    }
}

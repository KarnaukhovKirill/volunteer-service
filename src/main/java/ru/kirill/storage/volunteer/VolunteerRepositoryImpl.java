package ru.kirill.storage.volunteer;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;
import ru.kirill.storage.entity.Volunteer_;
import ru.kirill.storage.searchers.VolunteerSearcherer;
import ru.kirill.storage.updaters.VolunteerUpdater;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Repository
public class VolunteerRepositoryImpl implements VolunteerRepository {
    EntityManager entityManager;
    private final List<VolunteerUpdater<?>> updaters;
    private final List<VolunteerSearcherer<?>> searcherers;

    public VolunteerRepositoryImpl(EntityManager entityManager, List<VolunteerUpdater<?>> updaters, List<VolunteerSearcherer<?>> searcherers) {
        this.entityManager = entityManager;
        this.updaters = updaters;
        this.searcherers = searcherers;
    }

    @Override
    public List<Volunteer> get(VolunteerListRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Volunteer> query = criteriaBuilder.createQuery(Volunteer.class);
        Root<Volunteer> root = query.from(Volunteer.class);

        Predicate[] predicates = searcherers.stream()
                .map(searcher -> searcher.toPredicate(root, criteriaBuilder, request))
                .filter(Objects::nonNull)
                .toArray(Predicate[]::new);

        CriteriaQuery<Volunteer> select = query.select(root).where(predicates);
        TypedQuery<Volunteer> typedQuery = entityManager.createQuery(select);

        return typedQuery.getResultList();
    }

    @Transactional
    @Override
    public int update(String userId, UpdateVolunteerRequest request) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Volunteer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Volunteer.class);
        Root<Volunteer> root = criteriaUpdate.from(Volunteer.class);

        updaters.forEach(updater -> updater.setAttr(criteriaUpdate, request));
        criteriaUpdate.where(criteriaBuilder.equal(root.get(Volunteer_.userId), userId));

        return entityManager.createQuery(criteriaUpdate).executeUpdate();
    }
}

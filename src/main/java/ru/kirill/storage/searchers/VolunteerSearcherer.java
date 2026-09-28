package ru.kirill.storage.searchers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;

import java.util.function.Function;

@AllArgsConstructor
public class VolunteerSearcherer<V> {
    SingularAttribute<Volunteer, V> attribute;
    Function<VolunteerListRequest, V> function;

    /**
     * @return условие для where или null, если фильтр в запросе не задан
     */
    public Predicate toPredicate(Root<Volunteer> root, CriteriaBuilder criteriaBuilder, VolunteerListRequest request) {
        V attr = function.apply(request);
        if (attr == null) {
            return null;
        }
        return criteriaBuilder.equal(root.get(attribute), attr);
    }
}

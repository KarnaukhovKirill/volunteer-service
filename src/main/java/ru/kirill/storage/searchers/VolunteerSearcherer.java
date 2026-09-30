package ru.kirill.storage.searchers;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.SingularAttribute;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Volunteer;

import java.util.function.Function;

public class VolunteerSearcherer<V> {
    private final Function<VolunteerListRequest, V> function;
    private final PredicateFactory<V> predicateFactory;

    /**
     * Фильтр на точное совпадение атрибута со значением из запроса
     */
    public VolunteerSearcherer(SingularAttribute<Volunteer, V> attribute, Function<VolunteerListRequest, V> function) {
        this(function, (root, criteriaBuilder, value) -> criteriaBuilder.equal(root.get(attribute), value));
    }

    /**
     * Фильтр с произвольным условием
     */
    public VolunteerSearcherer(Function<VolunteerListRequest, V> function, PredicateFactory<V> predicateFactory) {
        this.function = function;
        this.predicateFactory = predicateFactory;
    }

    /**
     * @return условие для where или null, если фильтр в запросе не задан
     */
    public Predicate toPredicate(Root<Volunteer> root, CriteriaBuilder criteriaBuilder, VolunteerListRequest request) {
        V attr = function.apply(request);
        if (attr == null) {
            return null;
        }
        return predicateFactory.create(root, criteriaBuilder, attr);
    }

    @FunctionalInterface
    public interface PredicateFactory<V> {
        Predicate create(Root<Volunteer> root, CriteriaBuilder criteriaBuilder, V value);
    }
}

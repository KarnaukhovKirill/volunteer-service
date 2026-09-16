package ru.kirill.storage;

import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.metamodel.SingularAttribute;
import lombok.AllArgsConstructor;
import ru.kirill.controller.dto.UpdateVolunteerRequest;

import java.util.function.Function;

@AllArgsConstructor
public class VolunteerUpdater<V> {
    SingularAttribute<Volunteer, V> attribute;
    Function<UpdateVolunteerRequest, V> function;

    public void setAttr(CriteriaUpdate<Volunteer> criteriaUpdate, UpdateVolunteerRequest dto) {
        V attr = function.apply(dto);
        if (attr != null) {
            criteriaUpdate.set(attribute, attr);
        }
    }
}

package ru.kirill.storage.searchers;

import jakarta.persistence.criteria.Expression;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.kirill.controller.dto.STATUS;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.entity.Location;
import ru.kirill.storage.entity.Location_;
import ru.kirill.storage.entity.Volunteer_;

@Configuration
public class VolunteerSearcherers {
    private static final char LIKE_ESCAPE = '\\';

    /**
     * Только city: локации с названием city или city(любой район), без учёта регистра.
     * city и restrict: точное совпадение с city(restrict).
     */
    @Bean
    public VolunteerSearcherer<VolunteerListRequest> searchByLocation() {
        return new VolunteerSearcherer<>(
                request -> StringUtils.hasText(request.getCity()) ? request : null,
                (root, criteriaBuilder, request) -> {
                    Expression<String> name = root.join(Volunteer_.location).get(Location_.name);
                    if (StringUtils.hasText(request.getRestrict())) {
                        return criteriaBuilder.equal(name, Location.buildName(request.getCity(), request.getRestrict()));
                    }
                    String city = request.getCity();
                    HibernateCriteriaBuilder hibernateBuilder = (HibernateCriteriaBuilder) criteriaBuilder;
                    Expression<String> lowerName = criteriaBuilder.lower(name);
                    return criteriaBuilder.or(
                            criteriaBuilder.equal(lowerName, criteriaBuilder.lower(hibernateBuilder.value(city))),
                            criteriaBuilder.like(lowerName,
                                    criteriaBuilder.lower(hibernateBuilder.value(escapeLike(city) + "(%")), LIKE_ESCAPE)
                    );
                });
    }

    @Bean
    public VolunteerSearcherer<STATUS> searchByStatus() {
        return new VolunteerSearcherer<>(Volunteer_.status, VolunteerListRequest::getStatus);
    }

    /**
     * экранируем спецсимволы like, чтобы % и _ из запроса искались как обычные символы
     */
    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}

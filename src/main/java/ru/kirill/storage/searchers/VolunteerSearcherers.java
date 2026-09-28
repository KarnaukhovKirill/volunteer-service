package ru.kirill.storage.searchers;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import ru.kirill.controller.dto.STATUS;
import ru.kirill.controller.dto.VolunteerListRequest;
import ru.kirill.storage.LocationRep;
import ru.kirill.storage.entity.Location;
import ru.kirill.storage.entity.Volunteer_;

import java.util.UUID;

@Configuration
public class VolunteerSearcherers {
    private static final UUID NOT_EXISTING_LOCATION_ID = new UUID(0, 0);

    private final LocationRep locationRep;

    public VolunteerSearcherers(LocationRep locationRep) {
        this.locationRep = locationRep;
    }

    @Bean
    public VolunteerSearcherer<Location> searchByLocation() {
        return new VolunteerSearcherer<>(Volunteer_.location, request -> {
            if (!StringUtils.hasText(request.getCity()) || !StringUtils.hasText(request.getRestrict())) {
                return null;
            }
            String name = request.getCity() + "(" + request.getRestrict() + ")";
            // если локации нет, то и волонтёров в ней нет: подставляем несуществующий id, чтобы запрос вернул пустой список
            return locationRep.findByName(name)
                    .stream()
                    .findFirst()
                    .orElseGet(() -> Location.builder().id(NOT_EXISTING_LOCATION_ID).build());
        });
    }

    @Bean
    public VolunteerSearcherer<STATUS> searchByStatus() {
        return new VolunteerSearcherer<>(Volunteer_.status, VolunteerListRequest::getStatus);
    }
}

package ru.kirill.storage.updaters;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.kirill.controller.dto.UpdateVolunteerRequest;
import ru.kirill.storage.entity.Location;
import ru.kirill.storage.LocationRep;
import ru.kirill.storage.entity.Volunteer_;

import java.time.LocalDateTime;

@Configuration
public class VolunteerUpdaters {
    private final LocationRep locationRep;

    public VolunteerUpdaters(LocationRep locationRep) {
        this.locationRep = locationRep;
    }

    @Bean
    public VolunteerUpdater<String> lastNameVolunteerUpdater() {
        return new VolunteerUpdater<>(Volunteer_.lastName, UpdateVolunteerRequest::getLastName);
    }

    @Bean
    public VolunteerUpdater<Location> locationVolunteerUpdater() {
        return new VolunteerUpdater<>(Volunteer_.location, request -> {
            String name = request.getCity() + "(" + request.getRestrict() + ")";
            return locationRep.findByName(name)
                    .stream()
                    .findFirst()
                    .orElse(
                            locationRep.save(Location.builder().name(name).locationKind(Location.LOCATIONKIND.PARENT).createDate(LocalDateTime.now()).build())
                    );
        });
    }

    @Bean
    public VolunteerUpdater<LocalDateTime> updateDateVolunteerUpdater() {
        return new VolunteerUpdater<>(Volunteer_.updateDate, request -> LocalDateTime.now());
    }
}

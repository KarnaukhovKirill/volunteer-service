package ru.kirill.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.kirill.controller.dto.CreateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerInfoDto;
import ru.kirill.storage.VolunteerInfo;

//todo do mapping
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
public interface VolunteerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "")
    VolunteerInfoDto toDto(VolunteerInfo volunteerI);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "firstName", expression = "java(extractFirstName(request.getFio()))")
    @Mapping(target = "lastName", expression = "java(extractLastName(request.getFio()))")
    @Mapping(target = "middleName", expression = "java(extractMiddleName(request.getFio()))")
    VolunteerInfo toDomain(CreateVolunteerRequest request);

    default String extractFirstName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 3 ? parts[1] : null;
    }

    default String extractLastName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 3 ? parts[0] : null;
    }

    default String extractMiddleName(String fio) {
        if (fio == null || fio.trim().isEmpty()) {
            return null;
        }
        String[] parts = fio.trim().split("\\s+");
        return parts.length >= 3 ? parts[2] : null;
    }
}

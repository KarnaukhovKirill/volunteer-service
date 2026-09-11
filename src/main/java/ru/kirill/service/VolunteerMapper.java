package ru.kirill.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.kirill.controller.dto.CreateVolunteerRequest;
import ru.kirill.controller.dto.VolunteerInfoDto;
import ru.kirill.storage.VolunteerInfo;

import java.time.LocalDateTime;
import java.util.List;

//todo do mapping
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR,
        imports = LocalDateTime.class, uses = FioParser.class)
public interface VolunteerMapper {

    @Mapping(target = "id", ignore = true)
    VolunteerInfoDto toDto(VolunteerInfo volunteerI);

    List<VolunteerInfoDto> toDto(List<VolunteerInfo> volunteers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "AVAILABLE")
    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "createDate", expression = "java(LocalDateTime.now())")
    @Mapping(target = "currentIncidentId", ignore = true)
    @Mapping(target = "firstName", source = "fio", qualifiedByName = "extractFirstName")
    @Mapping(target = "lastName", source = "fio", qualifiedByName = "extractLastName")
    @Mapping(target = "middleName", source = "fio", qualifiedByName = "extractMiddleName")
    VolunteerInfo toDomain(CreateVolunteerRequest request);
}

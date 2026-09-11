package ru.kirill.storage;

import lombok.Builder;
import lombok.Data;
import ru.kirill.controller.dto.STATUS;
import ru.kirill.controller.dto.VolunteerInfoDto;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder(toBuilder = true)
public class VolunteerInfo {
    private final UUID id;
    private final String userId;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final STATUS status;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    //todo add location_id
    private final UUID currentIncidentId;

    public static VolunteerInfoDto toDto(VolunteerInfo volunteerInfo) {
        return VolunteerInfoDto.builder()
                .userId(volunteerInfo.userId)
                .firstName(volunteerInfo.firstName)
                .middleName(volunteerInfo.middleName)
                .createDate(volunteerInfo.createDate)
                .status(volunteerInfo.status)
                .build();
    }
}

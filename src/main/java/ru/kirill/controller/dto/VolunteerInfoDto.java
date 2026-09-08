package ru.kirill.controller.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VolunteerInfoDto {
    private final long id;
    private final String user_id;
    private final String firstName;
    private final String lastName;
    private final String middleName;
    private final STATUS status;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
}
